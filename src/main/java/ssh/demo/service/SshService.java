package ssh.demo.service;

import java.io.File;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.keys.ClientIdentityLoader;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

public class SshService {
	
	private TransportConfigCallback transportConfigCallback;
	private SshClient sshClient;
	private SshdSessionFactory sshdSessionFactory = null;
	private File privateKeyFile;
	
	//Use Saved SSH Keys
	public SshService() { 
		
		// Configure the SshClient with default client identity
        this.sshClient = SshClient.setUpDefaultClient();
        this.sshClient.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
        this.sshClient.start();

        File defaultSshDir = new File(FS.DETECTED.userHome(), "/.ssh");
        
		// Configure the SshdSessionFactory with the SshClient
        this.sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setSshDirectory(defaultSshDir)
                .build(null);

        // Ensure the session factory is not null before using it
        if (this.sshdSessionFactory == null) {
            throw new IllegalStateException("SSH session factory is null.");
        } 
        
        // Configure the transport to use the custom SshdSessionFactory
        this.transportConfigCallback = new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                if (transport instanceof SshTransport) {
                    ((SshTransport) transport).setSshSessionFactory(sshdSessionFactory);
                }
            }
        };
	}
	
	//Use Provided SSH Keys
	public SshService(String pubKey, String privKey) throws IOException, GeneralSecurityException{ 
		
		System.out.println("Taking key as param for SSH service");
		
	    // Set up SSH client
	    this.sshClient = SshClient.setUpDefaultClient();
	    this.sshClient.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
	    this.sshClient.start();
	    
	    File sshDir = new File(FS.DETECTED.userHome(), pubKey);
	    
	    // Create a temporary file to store the private key
	 	//this.privateKeyFile = new File(sshDir, "id_rsa");
	 	
	 	// Write the private key to the temporary file
	    //java.nio.file.Files.writeString(this.privateKeyFile.toPath(), privateKey);

	 	// Create SSH session factory
        this.sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setSshDirectory(sshDir)
				.setDefaultKeysProvider(f -> keyMethod(f, pubKey, privKey))
                .build(null);

	    // Ensure the session factory is not null before using it
	    if (this.sshdSessionFactory == null) {
	    	throw new IllegalStateException("SSH session factory is null.");
	    }

	    this.transportConfigCallback = new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                if (transport instanceof SshTransport) {
                    ((SshTransport) transport).setSshSessionFactory(sshdSessionFactory);
                }
            }
        };
	}

	
	// Returns necessary information for Git commands that require SSH verification
	public TransportConfigCallback getTransportConfigCallback() {
		return transportConfigCallback;
	}
	
	public void createKnownHostsFile(File sshDir) {
		// Need to do something to fix the issue with this...
		// Either 1. user needs to provide more information or 2. figure out workaround with SshdSessionFactory
	}
	
	// Helper method to stop SSH client when necessary
	public void stopService() throws IOException {
		if (this.sshClient.isStarted()) {
			this.sshClient.stop();
			this.sshdSessionFactory.close();
			System.out.println("SSH Service is stopped.");
		} else {
			System.out.println("SSH Service was not started");
		}
	}
	
	// Helper method to delete temporary key file... not super efficient and need some way to enforce
	public void deleteTempFile() {
		this.privateKeyFile.delete();
	}

	public static Iterable<KeyPair> keyMethod(File f, String pub, String priv) {
		try{
			//System.out.println("Called keyMethod");

			List<KeyPair> keyPairs = new ArrayList<>();

			keyPairs.add(new KeyPair(loadPublicKey(pub), loadPrivateKey(priv)));

			System.out.println("Created Keypair");

			//keyPairs.add(collecMyKeyFromMemory());

			return keyPairs;
		} catch (Exception e) {
			throw new IllegalStateException("Failed to create key pair: ", e);
		}
    }

	public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException, IOException {
		String cleanKey = stored
        .replaceAll("-----BEGIN PUBLIC KEY-----", "")
        .replaceAll("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", ""); // Remove newlines or spaces
		
		byte[] data = Base64.getDecoder().decode((cleanKey.getBytes()));
		
		X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
		
		KeyFactory fact = KeyFactory.getInstance("RSA");
		
		PublicKey pub = fact.generatePublic(spec);

		System.out.println("Created PublicKey: " + pub);

		return pub;
	}


	public static PrivateKey loadPrivateKey(String stored) throws GeneralSecurityException, IOException {
		String cleanKey = stored
        .replaceAll("-----BEGIN RSA PRIVATE KEY-----", "")
        .replaceAll("-----END RSA PRIVATE KEY-----", "")
        .replaceAll("\\s", ""); // Remove newlines or spaces

		byte[] data = Base64.getDecoder().decode(cleanKey.getBytes());

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);

        KeyFactory fact = KeyFactory.getInstance("RSA");

        PrivateKey priv = fact.generatePrivate(spec);

		System.out.println("Created PriavteKey: " + priv);

        Arrays.fill(data, (byte) 0);
        return priv;
   	}
}