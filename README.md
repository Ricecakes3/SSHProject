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

Steps to convert public RSA key to PEM file (accepted in Java program):
  1. $ssh-keygen -f .ssh/id_rsa.pub -e -m pem
  2. $touch .ssh/id_rsa_pkcs1.pub (and copy contents from above into id_rsa_pkcs1.pub)
  3. $openssl rsa -pubin -in .ssh/id_rsa_pkcs1.pub -RSAPublicKey_in -outform PEM -out .ssh/pubkey_x509.pem

Steps to convert private RSA key to PEM file (accepted in Java program):
  1. $ssh-keygen -p -m PEM -f ~/.ssh/id_rsa
  2. $openssl pkcs8 -topk8 -inform PEM -outform PEM -in .ssh/id_rsa -out .ssh/id_rsa_pkcs8.pem -nocrypt
     (To check that this worked use “$cat .ssh/id_rsa_pkcs8.pem”)
