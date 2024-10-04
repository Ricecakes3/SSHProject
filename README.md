# Performing Git Operations with SSH

This repository automates connecting to GitHub using SSH keys when they are (or aren't) in the default location (".ssh" directory).

## Getting Started:
  1. Generate keys & add public to GitHub
  2. Verify connection to GitHub in the CLI using "ssh -T git@github.com"
  3. Change methods in GitMain.java to fit what you want to do (functionality for "git clone (ssh)" & "git add, commit, & push" exists currently)
  4. Input your information to the GitService instantiation (line 17 of GitMain.java), which is as follows:

     a. The GitHub repository's SSH clone address (should be in the format of "git@github.com:youUserName/repoName.git")

     b. The path to the directory on your local machine where the repository should be cloned

     c. Your private key

     d. The specific path where the temporary file for the private key should be located on your local machine
