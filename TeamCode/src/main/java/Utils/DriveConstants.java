package Utils;

import com.arcrobotics.ftclib.kinematics.wpilibkinematics.MecanumDriveKinematics;

public class DriveConstants {

    public static MecanumDriveKinematics kinematics = new MecanumDriveKinematics(
            new com.arcrobotics.ftclib.geometry.Translation2d(0, 0),
            new com.arcrobotics.ftclib.geometry.Translation2d(0, 0),
            new com.arcrobotics.ftclib.geometry.Translation2d(0, 0),
            new com.arcrobotics.ftclib.geometry.Translation2d(0, 0)
    );
}
