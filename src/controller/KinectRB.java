package controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import lejos.pc.comm.NXTConnector;
import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

/*
 * Copyright 2011-2014, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 *
 * When this program is used for academic or research purposes, 
 * please cite the following article that introduced this Java library: 
 * 
 * A. Barmpoutis. "Tensor Body: Real-time Reconstruction of the Human Body 
 * and Avatar Synthesis from RGB-D', IEEE Transactions on Cybernetics, 
 * October 2013, Vol. 43(5), Pages: 1347-1356. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain this copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce this
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class KinectRB extends J4KSDK {

	VideoPanelRB viewer = null;
	JLabel user_icon = null;
	SkeletonProcess skel_process = null;
	NXTConnector conn = null;
	OutputStream dos;
	boolean userReady;
	int userIDTracked;
	int frameCount = 0;
	JTextArea console;
	int curMovement = 0;

	public KinectRB() {
		skel_process = new SkeletonProcess();
		conn = new NXTConnector();
	}

	public void setNXTconnection(NXTConnector conn) {
		this.conn = conn;
		dos = conn.getOutputStream();
	}

	public void setViewer(VideoPanelRB viewer) {
		this.viewer = viewer;
	}

	public VideoPanelRB getViewer() {
		return viewer;
	}

	public void setConsole(JTextArea console) {
		this.console = console;
	}

	public void setUserStat(JLabel user_icon) {
		this.user_icon = user_icon;
	}

	private ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	@Override
	public void onDepthFrameEvent(short[] depth_frame, byte[] body_index,
			float[] xyz, float[] uv) {

		if (viewer == null) {
			return;
		}

		DepthMap map = new DepthMap(getDepthWidth(), getDepthHeight(), xyz);

		map.setMaximumAllowedDeltaZ(0.5);

		if (uv != null)
			map.setUV(uv);

		viewer.map = map;
	}

	@Override
	public void onSkeletonFrameEvent(boolean[] skeleton_tracked,
			float[] joint_position, float[] joint_orientation,
			byte[] joint_status) {

		if (viewer == null || viewer.skeletons == null) {
			userReady = false;
			return;
		}

		for (int i = 0; i < getSkeletonCountLimit(); i++) {
			viewer.skeletons[i] = Skeleton.getSkeleton(i, skeleton_tracked,
					joint_position, joint_orientation, joint_status, this);
			if (viewer.skeletons[i].isTracked()) {
				userIDTracked = viewer.skeletons[i].getPlayerID();
			}
		}

		skel_process.setSkeleton(viewer.skeletons[userIDTracked]);

		if (userReady) {
			ImageIcon icon = createImageIcon("images/icon_yes_user.png",
					"User Detected!");
			user_icon.setIcon(icon);
			if (curMovement == skel_process.getToMovement()) {

			} else {
				curMovement = skel_process.getToMovement();
				console.append("Movement ID : " + curMovement + "\n");
			}

			try {
				dos.write(skel_process.getToMovement());
				dos.flush();
			} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// wait for wave
			userReady = skel_process.getWaveCompleted();
			System.out.println("User Not Ready");
		}

	}

	@Override
	public void onColorFrameEvent(byte[] color_frame) {
		if (viewer == null || viewer.videoTexture == null)
			return;
		viewer.videoTexture.update(getColorWidth(), getColorHeight(),
				color_frame);
	}

}
