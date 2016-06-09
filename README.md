# AuthAspect - OAuth 2.0 Autentication and Authorization using spring Aspects

this is a final project of the hebrew university course 67766 MODULARITY USING ASPECTS by prof. Shmuel Katz, Technion, israel.
the project impliments OAuth 2.0 protocol using aspects for easy adoption by any java application.

## Compile and installation
download and compile the package. we used JetBrains IDEA 13.0 using the Aspect plugin provided by jetbrains.
add the package to your project.

## Usage
### Authrization to facebook
1. you need first to open an app using the developers portal at faceook: https://developers.facebook.com/apps/ make sure you keep the AppId and AppSecret, you will need them later.
2. find out the premissions you need for your needs. you can look at facebook's reference here: https://developers.facebook.com/docs/facebook-login/permissions
3. you are all set to start coding the aspect in your application. 
add @FacebookCreds(clientId = "YourAppId", secret = "yourAppSecret", scope = "public_profile, email, user_hometown, YOUR OWN PREMISSION") above your Main class
4. add @FacebookAuth above any method you want the user to authorize before executing

### Authrization to Google
1. you need first to open an app using the google developers console: https://console.developers.google.com/ make sure you keep the AppId and AppSecret, you will need them later.
2. find out the premissions you need for your needs. you can look at google's reference here: https://developers.google.com/identity/protocols/googlescopes
3. you are all set to start coding the aspect in your application. 
add @GoogleCreds(clientId = "yourAppId", secret = "yourSecret", scope="profile https://www.googleapis.com/auth/tasks") above your Main class
4. add @GoogleAuth above any method you want the user to authorize before executing

### Persistent Access Tokens
1. Add the presistent annotations to the methods you want to save and read tokens in:
@PermanentAuth, @OneMinAuth

## Contibute
you are more then welcom to fork the code and edit it!

