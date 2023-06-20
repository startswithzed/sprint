# Sprint 
Sprint is an application that helps it's users achieve their goals by maintaining accountability and setting smaller task deadlines.
Users can: 
- Create goals with deadlines.
- Invite other users to be their mentors(2 per goal). Mentors help users complete their tasks and monitor progress made by the user. It helps create accountability. Mentors may also motivate the users to complete their goal within set deadline.
- Break up the overall goal into smaller tasks with their own dealine.
- Add submission for tasks which mentors can review. A task is only completed when it passes the review.
- Find other goals and request to join as mentor.

and more upcoming features like notifications, chat etc.
## Made with
- Spring boot
- Spring security
- MongoDB
- JWT by Auth0
- Spring Doc Openapi
- Heroku (CD)

## Try it out
Find the latest build [here](https://sprint-to-cloud.herokuapp.com/swagger-ui/index.html).
### Things to consider
- The application is deployed on a free tier by Heroku and may take a while respond to the first request if the application is sleeping.
- Endpoints with **[SECURED]** in the description need an Authorization token to be passed as a header. Unfortunately authorization headers aren't supported by swagger and hence won't work with the docs UI.
- You can still test the endpoints using this postman collection.[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/17279060-abfad01b-54a3-4a56-95c9-08c67f90f554?action=collection%2Ffork&collection-url=entityId%3D17279060-abfad01b-54a3-4a56-95c9-08c67f90f554%26entityType%3Dcollection%26workspaceId%3D63df8940-a7c9-4047-ac96-f414da02dc7b)

**Note: The application is still a work in progress.**
