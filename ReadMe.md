# Password Manager

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/sqlite-%2307405e.svg?style=for-the-badge&logo=sqlite&logoColor=white)
![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)
![JavaFX](https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=javafx&logoColor=white)

## Description
A secure, open-source, lightweight and completely offline desktop app solution for managing passwords

## Intended Users
### 🔐 Privacy-Conscious Users
* People who avoid cloud storage due to concerns over data breaches.
* Users who prefer local encryption over remote syncing.
* Those who don’t trust proprietary password managers (LastPass, 1Password, Bitwarden Cloud).

### 💻 Tech-Savvy & Open-Source Enthusiasts
* Linux users and self-hosting advocates.
* Developers and security researchers who prefer open-source software.
* People who like to audit and verify code for vulnerabilities.

### 🏢 Organizations with Strict Security Policies
* Companies that forbid cloud-based password storage for security reasons.
* Cybersecurity firms requiring air-gapped password management solutions.
* Government agencies, defense contractors, and critical infrastructure providers.

## Inspiration
Most password managers cost money, are closed-source and require internet connection (super sketchy). It is very important to trust your password managers, and what better way to build trust than reading the source code for yourself.

## How It Works
Upon first launch, you create your password. Using this password, your local SQLite database is created and encrypted. You are then shown the table of your passwords, where you can add/delete password records as you wish. These password records are encrypted using your user password, within the already-emcrypted database. And your user login password is unreversibly hashed within the database. Passwords are only shown when clicking "Show Password" within the UI. All sensitive information is hashed (SHA-256) and/or encrypted (AES).

## Images
<img width="450" alt="pwm-ss" src="https://github.com/user-attachments/assets/1b98c56d-d75a-4f2b-9fb2-ec9c0a425d0b" />

## Stack 
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/sqlite-%2307405e.svg?style=for-the-badge&logo=sqlite&logoColor=white)
![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)
![JavaFX](https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=javafx&logoColor=white)

## Backlog before first public release
* Extend capabilities of UI
* Style UI
* Packaging dependencies
* In-depth testing
