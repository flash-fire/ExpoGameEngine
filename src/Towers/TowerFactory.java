package Towers;

public class TowerFactory {
	public TowerFactory(){
	}

	public static AbstractTower newTower(int select) {
		AbstractTower tow;
		switch(select) {
		case 0:
			tow = new BasicTower();
			break;
		case 1:
			tow = new FlyingTower();
			break;
		case 2:
			tow = new AirBaseTower();
			break;
		case 3:
			tow = new SplashTower();
			break;
		case 4:
			tow = new SwordTower();
			break;
		case 5:
			tow = new FastTower();
			break;
		case 6:
			tow = new GasTower();
			break;
		case 7:
			tow = new RingTower();
			break;
		default:
			tow = new BasicTower();
		}
		tow.defaultStats();
		return tow;
	}
	public String name;
}
