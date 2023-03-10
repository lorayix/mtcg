# mtcg
## Own Thoughts
This is a project I have to do for my studies.  
It's a monster card trading game and because I'm sometimes a bit stupid when it comes to the sequence of letters, I misnamed the project.
First things first, I took way too long to start with this project. That's why some functions may seem a bit rough around the edges.
## Technical Steps
### Timeline
I've started by copying the functionality from the http-server we did in class and added a couple of necessary changes, like setting identifiers for some routes.  
After that, I tried to get the database connection to work, which was problematic since I wanted to use docker, like we did in class. The problem was that I also had it installed locally, which seemed to conflict with the docker instance. Once I had resolved that conflict, I could connect to the postgres db.  
Next I copied the structure for all other classes as I saw fit from reading the api file and comparing them to the structure we  had made in class and did it to the best of my understanding.  
For the functionality of the different classes I decided to split by different paths and go step by step from there.
### Functionality
I worked from Controller down to Repository for each of the different classes, as in I acted as though the deeper functionality in the database was given. Once I was happy with the controller functionality I went down to the repository, where I created the databases and their functionality.
### Problems
I ran into a few problems while creating the databases and controllers where I often mismatched data types and used to struggle for hours when I tried to insert a float into an integer in the database which it wouldn't let me do.  
The biggest problem I faced was with the battle functionality. I had tried for over 10+ hours to make it possible that when a battle is started the log gets returned to both players, however for the longest time only one thread at all would return. With the help of friends I finally managed to use the correct functions in the right order and nested code.
#### BattleController - enterFight
This was the function I struggled with. Early on I had figured out I needed an ArrayList for a lobby and also would need to synchronize. Adding, waiting etc. all worked how I expected them to, the fight worked too. What didn't work, however, was the return of both of the threads. In one thread, I would get a perfect log with a basic battle description back. In the other one, nothing happened. It appeared it got stuck somewhere.   
With the help of my friends I struggled through multiple tries and re-arrangements of code, until we found out what was missing: a monitoring object and after that, it took just a bit of debugging to find out where to put what code snippet exactly.   
## Design
My choices in design were not really thought over much - I copied much from the api file when it came to structure. The whole thing is split into two parts: **application** and **httpserver**. I will only go into detail over the application, since we did the httpserver in class.
### Application
The application is split into battle, config, controller, exception, model, repository, router, service and util. Most of these packages, except the battle, were already done in class, hence I won't explain the choice of the splits.
In the controller, the responses are formed based on the feedback the controllers get from the different repositories. Except for the battle one, that one works in the service.   
I mainly chose that form because I got the impression in class that this is the way it's supposed to be, and I tried to follow the standards which were set for us.
### Unique feature
My unique feature is something very simplistic because I was not very creative. Basically, there is a boolean which I call *"dealBreaker"* which gets activated every 25 rounds. When this boolean is activated, elements do play a role, no matter if there is a *SpellCard* drawn in the round or not, also the factor 2 which usually plays a role with elements is replaced by the factor 4, which makes it even more unlikely the weaker element would win. If the two cards are the same element, nothing happens.
## Unit tests
Most of the unit tests are in the battle category because to me, that is the most important part in this program. There are so many edge cases there, from the rock-paper-scissor algorithm with the different special cases, to the dead-end cases *(e.g. WaterSpell vs. knight)* which all are necessary to see.  
The other unit tests are controller tests to see whether basic functions like registering a user or login one or package/card creation because those functionalities are also necessary.  
I also wanted to see, whether the database would work or not, which is why I decided to test a few functions on that base as well.
# Technicalities
## Time Spent
I have tracked most of the time I spent on the coding, researching, troubleshooting, etc. and it has accumulated over 40 hrs based on the tracking program I used. I imagine most of this time was spent researching exceptions, errors and methods I did not know beforehand.  
![Tracking on Toggl](https://s20.directupload.net/images/230225/uazkbxz4.png "screenshot of time I took")
## [Link to Git](https://github.com/lorayix/mtcg)
