package controller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

import lejos.pc.comm.NXTConnector;
import edu.ufl.digitalworlds.gui.DWApp;
import guiApp.MainPanel;

/*
 * Copyright 2011-2014, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 *
 * When this program is used for academic or resea	rch purposes, 
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
@SuppressWarnings("serial")
public class RoboShopViewerApp extends DWApp implements ChangeListener {

	static KinectRB myKinect;
	VideoPanelRB main_panel;
	static JTextArea console;
	JSlider elevation_angle;

	JButton reconnect;

	public void GUIsetup(JPanel p_root) {
		/**
		 * if(System.getProperty("os.arch").toLowerCase().indexOf("64")<0) {
		 * if(DWApp.showConfirmDialog("Performance Warning", "<html><center><br>
		 * WARNING: You are running a 32bit version of Java.<br>
		 * This may reduce significantly the performance of this application.<br>
		 * It is strongly adviced to exit this program and install a 64bit
		 * version of Java.<br>
		 * <br>
		 * Do you want to exit now?</center>")) System.exit(0); }
		 */
		setLoadingProgress("Intitializing KinectRB...", 20);
		myKinect = new KinectRB();

		if (!myKinect.start(KinectRB.DEPTH | KinectRB.COLOR | KinectRB.SKELETON
				| KinectRB.XYZ | KinectRB.PLAYER_INDEX)) {
			DWApp.showErrorDialog(
					"ERROR",
					"<html><center><br>ERROR: The KinectRB device could not be initialized.<br><br>1. Check if the Microsoft's KinectRB SDK was succesfully installed on this computer.<br> 2. Check if the KinectRB is plugged into a power outlet.<br>3. Check if the KinectRB is connected to a USB port of this computer.</center>");
			// System.exit(0);
		}

		setLoadingProgress("Intitializing OpenGL...", 60);

		// GUI Elements Top
		JPanel headerLayout = new JPanel(new FlowLayout());
		JLabel icon_head = new JLabel();
		icon_head.setAlignmentX(Component.CENTER_ALIGNMENT);
		headerLayout.add(icon_head);
		// -- END

		// GUI Elements Middle
		main_panel = new VideoPanelRB();
		// -- END

		// GUI Element Bottom
		Container pane = new Container();
		pane.setLayout(new GridBagLayout());

		console = new JTextArea();
		console.setWrapStyleWord(true);
		console.setLineWrap(true);
		DefaultCaret caret = (DefaultCaret) console.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane consoleScroll = new JScrollPane(console);
		consoleScroll.setViewportView(console);
		consoleScroll.setWheelScrollingEnabled(true);
		consoleScroll.setFocusCycleRoot(true);

		JPanel iconLayout = new JPanel(new FlowLayout());
		JButton icon_nxt = new JButton();
		icon_nxt.addActionListener(new reconnectListener());
		JLabel icon_user = new JLabel();
		iconLayout.add(icon_nxt);
		iconLayout.add(icon_user);

		JLabel elevation_label = new JLabel("Camera Elevation : ");
		elevation_angle = new JSlider();
		elevation_angle.setSize(getPreferredSize());
		elevation_angle.setMinimum(-27);
		elevation_angle.setMaximum(27);
		elevation_angle.setValue((int) myKinect.getElevationAngle());
		elevation_angle.setToolTipText("Elevation Angle ("
				+ elevation_angle.getValue() + " degrees)");
		elevation_angle.addChangeListener(this);
		// -- END

		// Assign image to icon
		ImageIcon nxt = createImageIcon("images/nxt_connect.png",
				"Reconnect NXT");
		ImageIcon icon = createImageIcon("images/icon_no_user.png",
				"No User Detected!");
		ImageIcon header = createImageIcon("images/icon-robot-shop.png",
				"User Detected!");
		icon_nxt.setIcon(nxt);
		icon_head.setIcon(header);
		icon_user.setIcon(icon);
		// -- END

		// Assign GUI Element to Layout
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.25;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		pane.add(iconLayout, c);

		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		pane.add(consoleScroll, c);

		c.gridx = 0;
		c.gridy = 1;
		pane.add(elevation_label, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		pane.add(elevation_angle, c);

		// Assign Kinect elements and rules
		myKinect.setViewer(main_panel);
		myKinect.setUserStat(icon_user);
		myKinect.setConsole(console);
		myKinect.setSeatedSkeletonTracking(true);
		myKinect.computeUV(true);
		// -- END

		// Assign all layout to main frame
		p_root.add(headerLayout, BorderLayout.NORTH);
		p_root.add(main_panel, BorderLayout.CENTER);
		p_root.add(pane, BorderLayout.SOUTH);
		// -- END
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

	public void GUIclosing() {
		myKinect.stop();
	}

	public static boolean startKinect(NXTConnector NXTCon) {
		createMainFrame("RoboShop Viewer And Controller");
		app = new RoboShopViewerApp();
		setFrameSize(1366, 720, null);
		System.out.println("Connected to " + NXTCon.getNXTInfo() + "\n");
		myKinect.setNXTconnection(NXTCon);
		console.append("Connected to " + NXTCon.getNXTInfo() + "\n");		
		
		return NXTCon != null;
	}

	public static void main(String args[]) {
		createMainFrame("RoboShop Viewer And Controller");
		app = new RoboShopViewerApp();
		setFrameSize(1366, 720, null);
	}

	// Start NXT connection

	public void connect() {

		NXTConnector NXTCon = new NXTConnector();// create a new NXT connector

		boolean connected = NXTCon.connectTo("btspp://"); // connect NXT over
															// bluetooth
		// boolean connected = conn.connectTo("usb://");
		if (!connected) {// failure
			console.append("Failed to connect to any NXT\n");
			console.append("Press Reconect to retry.\n");
		}

		else// success!
		{
			System.out.println("Success");
			myKinect.setNXTconnection(NXTCon);
			console.append("Connected to " + NXTCon.getNXTInfo() + "\n");
			System.out.println("Connected to " + NXTCon.getNXTInfo() + "\n");
		}

	}

	class reconnectListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			NXTConnector NXTCon = (NXTConnector) MainPanel.getNXTConnect(null).getNXTComm();
			if (NXTCon.getNXTComm() != null) {
				console.append("Already connected\n");
			} else {
				connect();// call main connect method to try to connect again
			}
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == elevation_angle) {
			if (!elevation_angle.getValueIsAdjusting()) {
				myKinect.setElevationAngle(elevation_angle.getValue());
				elevation_angle.setToolTipText("Elevation Angle ("
						+ elevation_angle.getValue() + " degrees)");
				console.append("Kinect elevation changed : "
						+ myKinect.getElevationAngle() + "\n");
			}
		}
	}

}
