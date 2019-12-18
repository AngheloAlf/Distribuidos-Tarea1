public class Song
{
	protected String name;
	protected int duration;
	public int timeLeft;
	
	public Song(String name, int duration)
	{
		this.name = name;
		this.duration = duration;
		this.timeLeft = duration;
	}

	public String toString(){
		return name + "_" + timeLeft;
	}

	public void resetTimeLeft(){
		timeLeft = duration;
	}
}
