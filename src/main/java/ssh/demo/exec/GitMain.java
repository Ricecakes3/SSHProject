package ssh.demo.exec;

import ssh.demo.service.GitService;



/* 
 *  Class to execute clones/pushes from/to GitHub repository. Calls GitService.java to perform these operations.
 */
public class GitMain {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception{
		
		String publicKey = "Your Public Key";

		String privateKey = "Your Private Key";
		
		// Create Git Service object
		GitService gitService = new GitService("git@github.com:Ricecakes3/AutoGitHubRepo.git", "C:/Users/Connor Rice/Documents/GitHub/AutoGitHubRepo/");
		
		//Pick which way you want to connect, leaving out private key is meant for debug
		gitService.createSshService(publicKey, privateKey);
		//gitService.createSshService();

		// Pick which method you would like to use from GitService --> cloneRepository() OR addCommitPush()
		//gitService.cloneRepository();
		gitService.addCommitPush();
		
		// Delete temporary key file 
		//gitService.deleteTempFile();

		// Before the end of the main method, close out of SSH service
		gitService.stopSshService();
	}
}
