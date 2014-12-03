package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Match;
import model.Player;
import model.SimpleChat;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.*;
import play.mvc.Http.Request;
import views.js.ws;
import de.htwg.util.observer.IObserver;
import de.htwg.xiangqi.XiangqiGame;
import de.htwg.xiangqi.controller.IBoardManager;

public class Application extends Controller {

	private static int boardColSize = 9;
	private static int boardRowSize = boardColSize + 1;

	private static List<Match> matches = new ArrayList<Match>();
	private static Match lonelyMatch = null;

	private static List<Player> players = new ArrayList<Player>();
	private static int nextMatchId = 1;
	private static int nextPlayerId = 1;

	public static Result index() {
		players.add(new Player(nextPlayerId));
		response().setCookie("id", "" + nextPlayerId);
		nextPlayerId++;
		return ok(views.html.welcome.render());
	}

	public static Result playGame() {
		Match m;
		int cookieId = Integer.parseInt(request().cookie("id").value());
		if (lonelyMatch != null) {
			m = lonelyMatch;
			lonelyMatch = null;
		} else {
			m = new Match(nextMatchId++);
			lonelyMatch = m;
		}

		players.get(cookieId - 1).setMatch(m);
		m.addPlayer(players.get(cookieId - 1));
		return ok(views.html.index.render(transformStringToArrayList(m.getXg()
				.getTui().printBoard()), null, m.getBm().getPlayersTurn(), cookieId % 2));
	}

	// Websocket interface fro Chat
	public static WebSocket<String> wsInterface() {
		return new WebSocket<String>() {
			// called when websocket handshake is done
			public void onReady(WebSocket.In<String> in,
					WebSocket.Out<String> out) {
				SimpleChat.start(in, out);
			}
		};
	}

	// Websocket intrface for Observersocket
	public static WebSocket<String> getNewObserverSocket() {
		int cookieId = Integer.parseInt(request().cookie("id").value());
		Player p = players.get(cookieId - 1);

		WebSocket<String> ws = new WebSocket<String>() {
			@Override
			public void onReady(WebSocket.In<String> in,
					WebSocket.Out<String> out) {
				p.setIn(in);
				p.setOut(out);
				/* TODO eventhandling */
				SimpleChat.connections.add(out);

				in.onMessage(new Callback<String>() {
					public void invoke(String event) {
						p.getMatch().updateChat(event);
					}
				});

				in.onClose(new Callback0() {
					public void invoke() {
						p.getMatch().updateChat("Opponent closed Game");
					}
				});
			};
		};
		p.setWebSock(ws);
		return ws;
	};

	public static Result WUIupdate() {
		int cookieId = Integer.parseInt(request().cookie("id").value());
		Player p = players.get(cookieId - 1);
		XiangqiGame xg = p.getMatch().getXg();
		IBoardManager bm = xg.getBm();
		return ok(views.html.index.render(transformStringToArrayList(xg
				.getTui().printBoard()), null, bm.getPlayersTurn(), cookieId % 2));
	}

	// get the ws.js script
	public static Result wsJs() {
		return ok(views.js.ws.render());
	}

	public static Result input(String s) {
		int cookieId = Integer.parseInt(request().cookie("id").value());
		System.out.println("\n\ninput()OUT:\n" + cookieId + "\n\n");
		Player p = players.get(cookieId - 1);
		XiangqiGame xg = p.getMatch().getXg();
		IBoardManager bm = xg.getBm();
		int pID = cookieId % 2;
		int turn = bm.getPlayersTurn();

		if (pID == turn) {
			boolean checkmate = bm.inputMove(s);
			String msg = bm.getMessage();
			if (checkmate) {
				msg = bm.winnerMessage();
			}
			return ok(views.html.index.render(transformStringToArrayList(xg
					.getTui().printBoard()), msg, turn, pID));
		} else {
			return ok(views.html.index.render(transformStringToArrayList(xg
					.getTui().printBoard()), "Please wait! Opponent's turn...",
					turn, pID));
		}
	}

	/*
	 * Addittional Logic for converting Strings to Lists - needed so we dont
	 * have to screw around in scala
	 */
	public static ArrayList<ArrayList<String>> transformStringToArrayList(
			String s) {
		ArrayList<ArrayList<String>> returnVal = new ArrayList<ArrayList<String>>(
				boardRowSize);

		String rowRegex = "(?m)^[0-9] .*$";/*
											 * ^ Stringanfang $ Stringende (?m)
											 * multiline
											 */
		String colRegex = "[A-Z ]{2}";/* {i} i-times */
		Pattern rowPattern = Pattern.compile(rowRegex);
		Pattern colPattern = Pattern.compile(colRegex);

		Matcher rowMatcher = rowPattern.matcher(s);
		while (rowMatcher.find()) {
			ArrayList<String> tmpList = new ArrayList<String>();
			Matcher colMatcher = colPattern.matcher(rowMatcher.group());
			while (colMatcher.find()) {
				tmpList.add(colMatcher.group());
			}
			returnVal.add(tmpList);
		}
		return returnVal;
	}
}
