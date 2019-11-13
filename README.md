# Scoping Project with API, Database, and Deeplink

This project includes the API and Android app needed to complete the requirements of Scoping Project.

The API was create in Node.js and uses Express for the routing. The data is stored on an Amazon AWS MySQL database.

The API is hosted on Heroku on the web address: `https://scopingproject.herokuapp.com/`.

## Routes

There is a Postman file in this repository that contains all uses of the APIs

```/verifyToken```:

    https://scopingproject.herokuapp.com/verifyToken

This route takes in a JWT token received from the deeplink. The token is validated on the server, and returns either a passing or failing status code

```/login```:

    https://scopingproject.herokuapp.com/login

This route takes in an email and password. The email and password are compared to the database and the route either returns a passing or failing status code. There is also error checking that takes place (invalid email)

```/submit_score```:

    https://scopingproject.herokuapp.com/submit_score

This route takes in an email, team name, and score. There is checking that makes sure that the team exists. The data is added to the scores table of the database

```/personal_scores```:

    https://scopingproject.herokuapp.com/personal_scores

This route takes in an email address. The server checks the database for scores that the email has submitted and returns a list of the scores in json pairs of team, score

```/overall_scores```:

    https://scopingproject.herokuapp.com/overall_scores

This is the only GET API on the server. It checks all of the submitted scores and tallys up total points for each team. It divides the total score by the number of submissions to get an average. The list is sorted in descending order and sent back as a json object.

```/request_login_information```:

    https://scopingproject.herokuapp.com/request_login_information

This route takes in an email address. The server makes sure that the email address is valid. If it is, a JWT token is created with the email address and sent in an email to the address. From there the user clicks a link, pastes the JWT token that they were given, then clicks another link to be taken to the apps main page where they will have already been logged in. This is the deeplink/magic link section of the API

```/deeplink```:

    https://scopingproject.herokuapp.com/deeplink

Since Gmail will not allow us to send a suspicious link in an email (`myapp://myhost/login`), there must the an HTML page that the user can be redirected to. This is that page.



## Database Schema

The MySQL database on Amazon AWS is pretty basic.

Team Table

| team |
|------|

This is the table that holds all of the valid names for teams - teams that are competing in the event

Users Table

| email | password | firstname | lastname |
|-------|----------|-----------|----------|

This table contains all of the allowed users. These are the users that may submit scores to the application

Scores Table

| id | email | team | score |
|----|-------|------|-------|

This is the table that holds all of the submitted scores

## Data stored on the device

The email is the only thing that is stored in Shared Preferences. This is after a user logs in manually or uses a deeplink to log in.

## Video link

https://drive.google.com/file/d/1Tl0qaU_F7aZ9oEw_tdnFqOlvTG5GspZx/view?usp=sharing
