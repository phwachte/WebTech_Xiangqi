package controllers;

import de.htwg.xiangqi.XiangqiGame;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	private static XiangqiGame xg;

	public static Result index() {
		xg = new XiangqiGame();
		xg.getBm().getBoard();

		return ok(views.html.index.render(xg.getTui().printBoard()));
	}

	public static Result input(String s) {
		boolean b = xg.getBm().inputMove(s);
		String msg = xg.getBm().getMessage();
		if (msg == null) {
			return ok(views.html.index.render(xg.getTui().printBoard()));
		} else {
			return ok(views.html.index.render(xg.getBm().getMessage()));
		}
	}

}
