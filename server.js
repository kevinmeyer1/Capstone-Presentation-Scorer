const express = require('express')
const app = express()
const mysql = require('mysql')
const bodyParser = require('body-parser')
const config = require('./config.json')

const nodemailer = require('nodemailer')
const xoauth2 = require('xoauth2')

const jwt = require('jsonwebtoken')

var mySQLUsername = config['mySQLUsername']
var mySQLPassword = config['mySQLPassword']
var mySQLHost = config['mySQLHost']
var mySQLPort = config['mySQLPort']
var mySQLDatabase = config['mySQLDatabase']

var spEmail = config['scopingProjectEmail']
var clientId = config['clientId']
var clientSecret = config['clientSecret']
var refreshToken = config['refreshToken']
var accessToken = config['accessToken']

var jwtSecretKey = 'secret'

//scopingproject@gmail.com
//^BC+r259,F

//Heroku code to set the listening port
const PORT = process.env.PORT || 3000

// ------------------------------------------------- CONNECTIONS  -------------------------------------------------

var con = mysql.createConnection({
    host: mySQLHost,
    user: mySQLUsername,
    password: mySQLPassword,
    database: mySQLDatabase
})

// -------------------------------------------------- FUNCTIONS  --------------------------------------------------

function sendEmail(toEmail, token, password, req, res) {
    var trans = nodemailer.createTransport({
        host: 'smtp.gmail.com',
        port: 465,
        secure: true,
        auth: {
            type: 'OAuth2',
            user: spEmail,
            clientId: clientId,
            clientSecret: clientSecret,
            refreshToken: refreshToken,
            accessToken: accessToken
        }
    })

    var mailOptions = {
        from: 'scopingproject@gmail.com',
        to: toEmail,
        subject: 'Scoping Project Login Information',
        html: `<h2>Login Information</h2>
        <p>Thank you for helping our students.
        Step 1. Copy the token provided in this email
        Step 2. Click the link in this email
        Step 3. Paste your token in the textbox on the following link
        Step 4. Click the link that appears on the webpage after submitting your token
        Step 5. You are logged into the app and taken to the main page</p>

        <h3>Token: ${token}</h3>

        <a href="https://scopingproject.herokuapp.com/deeplink">Click this link</a>

        <p>
        For future reference here is your login credentials\n
        Email: ${toEmail}\n
        Password: ${password}\n
        </p>
        `
    }

    trans.sendMail(mailOptions, function(err, info) {
        if (err) {
            console.log(err)
            res.status(401)
            res.setHeader('Content-Type', 'text/plain')
            res.write(`Error while sending email to ${toEmail}`)
            res.send()
        } else {
            res.status(200)
            res.setHeader('Content-Type', 'text/plain')
            res.write(`Login information sent to ${toEmail}`)
            res.send()
        }
    })
}


// ---------------------------------------------------- ROUTES ----------------------------------------------------

app.use(bodyParser.json())

app.post('/verifyToken', function(req, res) {
    var token = req.body.token

    jwt.verify(token, jwtSecretKey, function(err, decodedPayload) {
        if (err) {
            console.log(err)
            res.status(401)
            res.setHeader('Content-Type', 'text/plain')
            res.write('Token was not verified')
            res.send()
        } else {
            var data = {
                email: decodedPayload['email']
            }

            res.status(200)
            res.setHeader('Content-Type', 'application/json')
            res.json(data)
        }
    })
})

app.post('/login', function(req, res) {
    var email = req.body.email
    var password = req.body.password

    var loginQuery = 'SELECT * FROM users WHERE email = ? AND password = ?'

    con.query(loginQuery, [email, password], function(err, result, fields) {
        if (err) {
            //this shouldnt happen - error with query code
            console.log(err)
            res.status(401)
            res.setHeader('Content-Type', 'text/plain')
            res.write('Unauthorized')
            res.send()
        } else {
            if (result.length == 1) {
                res.status(200)
                res.setHeader('Content-Type', 'text/plain')
                res.write(`Successfully logged in as ${email}`)
                res.send()
            } else {
                console.log(result)
                res.status(401)
                res.setHeader('Content-Type', 'text/plain')
                res.write('Unauthorized')
                res.send()
            }
        }
    })
})

app.post('/submit_score', function (req, res) {
    var email = req.body.email
    var team = req.body.team
    var score = req.body.score

    var checkTeamQuery = 'select * from teams where team = ?'
    var submitScoreQuery = 'insert into scores values (DEFAULT, ?, ?, ?)'

    con.query(checkTeamQuery, [team], function(err, result, fields) {
        if (result.length == 1) {
            //team exists
            con.query(submitScoreQuery, [email, team, score], function(err, result, fields) {
                if (err) {
                    console.log(err)
                    res.status(500)
                    res.setHeader('Content-Type', 'text/plain')
                    res.write('Error while adding score to database')
                    res.send()
                } else {
                    res.status(200)
                    res.setHeader('Content-Type', 'text/plain')
                    res.write(`Score (${score}) added to team ${team} by ${email}`)
                    res.send()
                }
            })
        } else {
            //team does not exist
            res.status(406)
            res.setHeader('Content-Type', 'text/plain')
            res.write('Invalid team name')
            res.send()
        }
    })
})

app.post('/personal_scores', function(req, res) {
    var email = req.body.email

    var personalScoresQuery = 'select team, score from scores where email  = ?'

    con.query(personalScoresQuery, [email], function(err, result, fields) {
        if (err) {
            console.log(err)
            res.status(500)
            res.setHeader('Content-Type', 'text/plain')
            res.write('Error while gathering scores')
            res.send()
        } else {
            res.status(200)
            res.setHeader('Content-Type', 'application/json')
            res.json(result)
        }
    })
})

app.get('/overall_scores', function(req, res) {
    var overallScoresQuery = 'select team, score from scores'

    var teamScoresMap = new Map()
    var teamCountMap = new Map()
    var teamAverages = new Map()

    var returnJson = []

    con.query(overallScoresQuery, function(err, result, fields) {
        if (err) {
            console.log(err)
            res.status(500)
            res.setHeader('Content-Type', 'text/plain')
            res.write('Error while gathering scores')
            res.send()
        } else {
            result.forEach(function(row) {
                var team = row['team']
                var score = row['score']

                if (teamScoresMap.has(team)) {
                    //if in score map - in both - add score to total and increase count

                    var currentScore = teamScoresMap.get(team)
                    currentScore += score
                    teamScoresMap.set(team, currentScore)

                    var currentCount = teamCountMap.get(team)
                    currentCount += 1
                    teamCountMap.set(team, currentCount)
                } else {
                    //not in map, add score and set count to 1

                    teamScoresMap.set(team, score)
                    teamCountMap.set(team, 1)
                }
            })

            //get the average for each team by dividing the score by the count and put in new map
            teamScoresMap.forEach(function(score, team) {
                var totalCount = teamCountMap.get(team)

                var averageScore = (score / totalCount).toFixed(2)

                teamAverages.set(team, averageScore)
            })

            //put map into json array
            teamAverages.forEach(function(score, team) {
                var data = {
                    team: team,
                    score: score
                }

                returnJson.push(data)
            })

            res.status(200)
            res.setHeader('Content-Type', 'application/json')
            res.json(returnJson)
        }
    })


})

app.post('/request_login_information', function(req, res) {
    var email = req.body.email

    var checkEmailQuery = 'SELECT email, password FROM users WHERE email= ?'

    con.query(checkEmailQuery, [email], function(err, result, fields) {
        if (err) {
            console.log(err)
            res.status(401)
            res.setHeader('Content-Type', 'text/plain')
            res.write('Unauthorized')
            res.send()
        } else {
            if (result.length == 1) {
                var options = {
                    email: `${email}`
                }

                var token = jwt.sign(options, jwtSecretKey)

                //call function to send email to user
                sendEmail(email, token, result[0]['password'], req, res)
            } else {
                console.log(result)
                res.status(401)
                res.setHeader('Content-Type', 'text/plain')
                res.write('This email address does not have permission to request login information')
                res.send()
            }
        }
    })
})

app.get('/deeplink', function(req, res) {
    res.sendFile('/deeplink.html', {root: __dirname })
})

app.listen(PORT, () => {
    console.log(`Our app is running on port ${ PORT }`);
})
