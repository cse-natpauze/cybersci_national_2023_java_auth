# Software Bug (Pwn lite) Challenge
Development was done in https://github.com/cse-natpauze/cybersci_national_2023_java_auth
Ill make that repo public after the competition 
## Difficulty
Med

## Name
auth-to-me

## Description
There is a legacy java application being used by the AI to store some secrets... If we had valid creds we could dump a secret value from the application! The AI must be hiding something big there... 
We have release builds of the server and a old client... Good luck!

## Flag Question
What is the secret you recovered from the server?

## Hint
It's allways in the details....

## Walkthrough
Players will need to decomp the proguarded server and client, decomp the native lib used in logging, identify a bug that can be used to replace parts of secrets with known values.

TLDR: the native lib assumes it always gets a COPY of the backing buffer of java strings. That is not allways true. 
Reading the HOTSPOT JNI sources can help figure out what that condition is (SPOILER: string have chars not Latin1 encodable in it)
And figure out how to trigger that to gain credentials that work. They will need to examine decomp to find the account provisioning command (not included in client) and how commands are sent. Then trigger the bug to mangle the user/password of a new account, and guess the few remaining random bytes. 

More details in POC.java (dont distribute!)

## Flag Answer
whatever you set as the CTFKEY env variable when launching the server...

