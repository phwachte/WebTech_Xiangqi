import org.pac4j.core.client.Clients;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.play.Config;

import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {
	@Override
	public void onStart(final Application app) {
		// OAuth
		final Google2Client google2client = new Google2Client(
				"874845075147-jt9ik5o4a9dq5umhb60939kkp4t8naqg.apps.googleusercontent.com",
				"HjieGVsUSfinBNH0ebIAu6VG");

		final Clients clients = new Clients(
				"https://htwg-xiangqi.herokuapp.com/callback", google2client);
		Config.setClients(clients);
	}
}
