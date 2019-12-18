import java.util.LinkedList;
import java.util.Queue;

class Singer extends Thread
{
	volatile protected MulticastHelper socket;
	volatile protected Queue<Song> queue;

	volatile public Boolean keepRunning = true;
	protected String status;
	protected Song actualSong;

	Singer(MulticastHelper socket)
	{
		this.socket = socket;
		this.queue = new LinkedList<>();

		this.status = "stopped";
		this.actualSong = null;
	}

	public void run()
	{
		while (keepRunning) {
			try {
				Thread.sleep(1000);
				if (status.equals("playing")) {
					if (actualSong != null) {
						socket.send(actualSong.toString());
						actualSong.timeLeft--;

						if (actualSong.timeLeft <= 0) {
							if (queue.size() > 0) {
								actualSong = queue.remove();
							} else {
								actualSong = null;
								status = "stopped";
							}
						}
					} else {
						status = "stopped";
					}
				} else {
					if(status.equals("paused")) {
						socket.send("Reproduccion pausada. Ingrese 'PAUSE' para reanudar la reproduccion");
					} else {
						socket.send(status);
					}
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.out.println("Se ha hallado un error en el cantador:");
				System.out.println(e.toString());
				keepRunning = false;
			}
		}
	}

	public void play(Song song)
	{
		actualSong = song;
		status = "playing";
	}
	
	public void stopSinger()
	{
		while (queue.size() != 0) queue.remove();
		actualSong = null;
		status = "stopped";
	}

	public void pause()
	{
		if (status.equals("paused")) {
			status = "playing";
		} else {
			status = "paused";
		}
	}

	public void queueAdd(Song song)
	{
		queue.add(song);
	}

	public Song[] queueList()
	{
		if (queue.size() == 0){
			return null;
		}

		Queue<Song> auxQueue = new LinkedList<>();
		Song[] list = new Song[queue.size()];

		for (int i = 0; queue.size() > 0; ++i) {
			Song auxSong = queue.remove();
			list[i] = auxSong;
			auxQueue.add(auxSong);
		}
		while (auxQueue.size() > 0){
			queue.add(auxQueue.remove());
		}

		return list;
	}

	public void next()
	{
		if (queue.size() > 0) {
			actualSong = queue.remove();
		} else {
			actualSong = null;
			status = "stopped";
		}
	}
	
	public void jump(int index)
	{
		if (index == 0) {
			if (actualSong != null) {
				actualSong.resetTimeLeft();
			}
		} else {
			if (index < 0){
				index = queue.size() + 1 + index;
			}
			while (index-- > 0) {
				if (queue.size() > 0) {
					actualSong = queue.remove();
				} else {
					status = "stopped";
					index = 0;
				}
			}
		}
	}

}
