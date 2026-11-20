package Utils;

import com.arcrobotics.ftclib.kinematics.wpilibkinematics.MecanumDriveKinematics;

public class Constants {

    public static class Speeds {
        public static double flyWheelSpeed = 50.0;
    }

    public static class Turret {
        public static double kP = 12.0;
        public static double kI = 0.0;
        public static double kD = 0.0;
    }

    public static MecanumDriveKinematics kinematics = new MecanumDriveKinematics(
            new com.arcrobotics.ftclib.geometry.Translation2d(0, 0),
            new com.arcrobotics.ftclib.geometry.Translation2d(0, 0),
            new com.arcrobotics.ftclib.geometry.Translation2d(0, 0),
            new com.arcrobotics.ftclib.geometry.Translation2d(0, 0)
    );
}
