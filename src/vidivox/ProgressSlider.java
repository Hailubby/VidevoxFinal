package vidivox;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

@SuppressWarnings("serial")
public class ProgressSlider extends JSlider {
	private EmbeddedMediaPlayer video;
	private long length;
	
	private boolean playing;
	
	public ProgressSlider(final MediaPlayer frame){
		super();
		this.setValue(0);
		this.setMaximum(10000);
				
		final ProgressSlider thisSlider = this;
		ChangeListener l = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//checks boolean so statechanged is only 'activated' when we move the slider to new position
				if (playing) {
					return;
				}
				if(!frame.checkIfVideo(false)){
					thisSlider.setValue(0);
					return;
				}
				
				float position = thisSlider.getValue();
				
				if(position == 10000f){
					video.setPosition(0.999f);
				} else {
					video.setPosition(position/10000);
				}
			}
		};
		
		this.addChangeListener(l);
	}
		
	public void setVideo(EmbeddedMediaPlayer video){
		this.video = video;
	}
	
	public void setLength(){
		this.length = video.getLength();
	}
		
	public void updateSlider(long currentTime){
		
		if(length != 0){
			long sliderPosition =  (currentTime*10000 /length);
			playing = true;
			this.setValue((int)sliderPosition);
			playing = false;	
		}
	}
}