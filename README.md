
# AlzCare
<h5>App that aims to help people with Alzheimer's disease and their caregivers.</h5>

## Product Requirements

Goal: Allow user to successfully log in and view their details

## Functional Requirements and User Stories

|  | User Story | Requirement |Priority|Status|
|---|---|---|---|---|
|Create a new account|As a new user I want to be able to create an account|The system must allow user to create a new account using details like their name, email address and a password|Must Have|Completed|
|Login on Device|As an existing user I want to be able to login to my account|The system must allow users to login using their email and password|Must Have|Completed|
|Login as a patient or caregiver|As a user I want to be able to login as aa patient or a caregiver|The system must allow the user to login either as a patient or a caregiver|Must Have|Completed|
|Connect caregiver and patient|As a caretaker I want to enter my patient's name and be able to connect to their device|The system must be able to connect caregiver to the patient through unique keys in the database|Must Have|Completed|
|Display Patient Details|As an existing user I want to be able to see patient details on my phone|The system must fetch data from the database and display it on the device|Must Have|Completed|
|Display patient location|As a caregiver I want to see my patient's location|The system must be able to fetch location data from patient's device and display it on the caregiver's device|Must Have|Not Completed|
|Set Geofence|As a caregiver I want to set a geofence around my patient's location|The system must be able to fetch location data and set geofence around that location|Must Have|Not Completed|
|Send Geofence alert|As a caregiver I want to recieve an alert when my patient crosses the geofence boundary|The system must send out a notification alert when the patient's device crosses the geofence boundary set by the caregiver|Must Have|Not Completed|

## Screenshots of the application

## Main page and sign in page

This is the main page that opens when you click on the application on your phone

After choosing sign in with email it takes you to the sign in page

![alt text](https://github.com/KalpakGaonkar/alzApp/blob/main/Screenshots/Home_page_and_signin_page.png)

## Option to choose patient or caregiver and Login page

After signing in, the application lets you choose to be either a patient or a caregiver and opens a log in page

![alt text](https://github.com/KalpakGaonkar/alzApp/blob/main/Screenshots/patient_caregiver_option_and_login_page.png)

## Patient and Caregiver Home Page

On the left is the patient home page which has features like create and remove reminder and the option of signing out

On the right is the caregiver home page which has features such as Adding a Patient, Viewing patient details, View geofences (Not Completed Yet) and the option of signing out

![alt text](https://github.com/KalpakGaonkar/alzApp/blob/main/Screenshots/patient_and_caregiver_home_page.png)

## List of Patients

This page displays the list of patient which is linked. A patient can be added using the "+" sign on the bottom right corner

![alt text](https://github.com/KalpakGaonkar/alzApp/blob/main/Screenshots/list_of_patients.png)

## Patient Details

This page displays the added patient's details which include updates from the doctor, the current stage of Alzheimer's and the MRI scan

![alt text](https://github.com/KalpakGaonkar/alzApp/blob/main/Screenshots/patient_details.png)

