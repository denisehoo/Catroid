/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.common.SoundInfo;

public class Sprite implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private List<Script> scriptList;
	private ArrayList<CostumeData> costumeDataList;
	private ArrayList<SoundInfo> soundList;
	public transient Costume costume;

	public transient boolean isPaused;
	public transient boolean isFinished;

	private Object readResolve() {
		//filling FileChecksumContainer:
		if (soundList != null && costumeDataList != null && ProjectManager.getInstance().getCurrentProject() != null) {
			FileChecksumContainer container = ProjectManager.getInstance().fileChecksumContainer;
			if (container == null) {
				ProjectManager.getInstance().fileChecksumContainer = new FileChecksumContainer();
			}
			for (SoundInfo soundInfo : soundList) {
				container.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
			}
			for (CostumeData costumeData : costumeDataList) {
				container.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
			}
		}
		init();
		return this;
	}

	private void init() {
		costume = new Costume(this);
		isPaused = false;
		isFinished = false;
		if (soundList == null) {
			soundList = new ArrayList<SoundInfo>();
		}
		if (costumeDataList == null) {
			costumeDataList = new ArrayList<CostumeData>();
		}
	}

	public Sprite(String name) {
		this.name = name;
		scriptList = new ArrayList<Script>();
		costumeDataList = new ArrayList<CostumeData>();
		soundList = new ArrayList<SoundInfo>();
		init();
	}

	public void startWhenScripts(String action) {
		for (Script s : scriptList) {
			if (s instanceof WhenScript) {
				if (((WhenScript) s).getAction().equalsIgnoreCase(action)) {
					startScript(s);
				}
			}
		}
	}

	public void startStartScripts() {
		for (Script s : scriptList) {
			if (s instanceof StartScript) {
				if (!s.isFinished()) {
					startScript(s);
				}
			}
		}
	}

	public void startTapScripts() {
		for (Script s : scriptList) {
			if (s instanceof TapScript) {
				startScript(s);
			}
		}
	}

	private void startScript(Script s) {
		final Script script = s;
		Thread t = new Thread(new Runnable() {
			public void run() {
				script.run();
			}
		});
		t.start();
	}

	public void startScriptBroadcast(Script s, final CountDownLatch simultaneousStart) {
		final Script script = s;
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					simultaneousStart.await();
				} catch (InterruptedException e) {
				}
				script.run();
			}
		});
		t.start();
	}

	public void startScriptBroadcastWait(Script s, final CountDownLatch simultaneousStart, final CountDownLatch wait) {
		final Script script = s;
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					simultaneousStart.await();
				} catch (InterruptedException e) {
				}
				script.run();
				wait.countDown();
			}
		});
		t.start();
	}

	public void pause() {
		for (Script s : scriptList) {
			s.setPaused(true);
		}
		this.isPaused = true;
	}

	public void resume() {
		for (Script s : scriptList) {
			s.setPaused(false);
		}
		costume.resume();
		this.isPaused = false;
	}

	public void finish() {
		for (Script s : scriptList) {
			s.setFinish(true);
		}
		this.isFinished = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addScript(Script script) {
		if (script != null && !scriptList.contains(script)) {
			scriptList.add(script);
		}
	}

	public void addScript(int index, Script script) {
		if (script != null && !scriptList.contains(script)) {
			scriptList.add(index, script);
		}
	}

	public Script getScript(int index) {
		return scriptList.get(index);
	}

	public int getNumberOfScripts() {
		return scriptList.size();
	}

	public int getScriptIndex(Script script) {
		return scriptList.indexOf(script);
	}

	public void removeAllScripts() {
		scriptList.clear();
	}

	public boolean removeScript(Script script) {
		return scriptList.remove(script);
	}

	public ArrayList<CostumeData> getCostumeDataList() {
		return costumeDataList;
	}

	public ArrayList<SoundInfo> getSoundList() {
		return soundList;
	}

	@Override
	public String toString() {
		return name;
	}
}
