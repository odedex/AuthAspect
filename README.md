# AuthAspect - OAuth 2.0 Autentication and Authorization using Spring AOP

This is a final project of the Hebrew University course 67766 MODULARITY USING ASPECTS by prof. Shmuel Katz, Technion, israel.
The project impliments OAuth 2.0 protocol using aspects for easy adoption by any java application.

## Compile and installation
Download and compile the package. we used JetBrains IDEA 13.0 using the Aspect plugin provided by JetBrains.
Add the package to your project.

## Usage
### Authrization to facebook
1. Open an app using the developers portal at faceook: https://developers.facebook.com/apps/ make sure you keep the AppId and AppSecret, you will need them later.
2. Determine the premissions your app needs. You can look at facebook's reference here: https://developers.facebook.com/docs/facebook-login/permissions
3. You are all set to start coding the aspect in your application. 
add @FacebookCreds(clientId = "YourAppId", secret = "yourAppSecret", scope = "public_profile, email, user_hometown, YOUR OWN PREMISSION") above your Main class (i.e. the one with public static void main).
4. Add @FacebookAuth above any method you want the user to authorize before executing

### Authrization to Google
1. Open an app using the google developers console: https://console.developers.google.com/ make sure you keep the AppId and AppSecret, you will need them later.
2. Determine the premissions your app needs. you can look at google's reference here: https://developers.google.com/identity/protocols/googlescopes
3. You are all set to start coding the aspect in your application. 
add @GoogleCreds(clientId = "yourAppId", secret = "yourSecret", scope="profile https://www.googleapis.com/auth/tasks") above your Main class (i.e. the one with public static void main).
4. Add @GoogleAuth above any method you want the user to authorize before executing

### Persistent Access Tokens
Will save the authentication tokens recieved by the authentication service locally on disk.
1. Add the presistent annotations to the methods you want to save and read tokens in:
@PermanentAuth, @OneMinAuth

## Contibute
you are more than welcome to fork the code and edit it!

