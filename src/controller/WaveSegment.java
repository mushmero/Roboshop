package controller;

import edu.ufl.digitalworlds.j4k.Skeleton;

public class WaveSegment {
	
	public WaveSegment(){
		
	}
	
	public boolean wavePartLeft(Skeleton skeleton){
		if(skeleton.get3DJointY(Skeleton.HAND_RIGHT) > skeleton.get3DJointY(Skeleton.ELBOW_RIGHT)){
			if(skeleton.get3DJointX(Skeleton.HAND_RIGHT) < skeleton.get3DJointX(Skeleton.ELBOW_RIGHT)){
				return true;
			}
		}
		return false;
	}
	
	public boolean wavePartRight(Skeleton skeleton){
		if(skeleton.get3DJointY(Skeleton.HAND_RIGHT) > skeleton.get3DJointY(Skeleton.ELBOW_RIGHT)){
			if(skeleton.get3DJointX(Skeleton.HAND_RIGHT) > skeleton.get3DJointX(Skeleton.ELBOW_RIGHT)){
				return true;
			}
		}
		return false;
	}
	
	
}
