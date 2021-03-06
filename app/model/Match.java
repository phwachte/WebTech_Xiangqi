package model;

import de.htwg.util.observer.IObserver;
import de.htwg.xiangqi.XiangqiGame;
import de.htwg.xiangqi.controller.IBoardManager;

public class Match implements IObserver {

	private int matchID;
	private Player p1, p2;
	private XiangqiGame xg;
	private IBoardManager bm;

	public Match(int id) {
		this.matchID = id;
		xg = new XiangqiGame();
		bm = xg.getBm();
		bm.addObserver(this);
	}

	public void addPlayer(Player player) {
		if (p1 == null) {
			p1 = player;
		} else {
			p2 = player;
		}
	}

	@Override
	public void update() {
		switch (p1.getStat()) {
		case ONGOING:
			p1.getOut().write("updateNow");
			break;
		case WON:
			p1.getOut().write("youWon");
			break;
		case LOST:
			p1.getOut().write("youLost");
			break;

		default:
			break;
		}

		switch (p2.getStat()) {
		case ONGOING:
			p2.getOut().write("updateNow");
			break;
		case WON:
			p2.getOut().write("youWon");
			break;
		case LOST:
			p2.getOut().write("youLost");
			break;

		default:
			break;
		}
	}

	public void updateChat(Player player, String s) {
        if(s.equals("ping")){
            System.out.println("########: ping");
        }else{
            p1.getOut().write("["+player.getName()+"]: "+s);
            p2.getOut().write("["+player.getName()+"]: "+s);
            System.out.println("########UpdateChat: " + s);
        }
	
	}

	/*
	 * 
	 * GETTER AND SETTER
	 */

	public Player getP1() {
		return p1;
	}

	public void setP1(Player p1) {
		this.p1 = p1;
	}

	public Player getP2() {
		return p2;
	}

	public void setP2(Player p2) {
		this.p2 = p2;
	}

	public int getMatchID() {
		return matchID;
	}

	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}

	public XiangqiGame getXg() {
		return xg;
	}

	public void setXg(XiangqiGame xg) {
		this.xg = xg;
	}

	public IBoardManager getBm() {
		return bm;
	}

	public void setBm(IBoardManager bm) {
		this.bm = bm;
	}

}
