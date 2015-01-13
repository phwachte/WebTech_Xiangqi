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
				"874845075147-ggteisd258f5hosu68qnlm9u41sv1oat.apps.googleusercontent.com",
				"N9vLFudbGo5ACD6JdXCvLy3S");

		final Clients clients = new Clients(
				"https://htwg-xiangqi.herokuapp.com/callback", google2client);
		Config.setClients(clients);
	}
}
