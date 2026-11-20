package Utils;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.kinematics.wpilibkinematics.MecanumDriveKinematics;

public class Constants {

    @Config
    public static class Speeds {
        public static double flyWheelSpeed = 50.0;

        public static double fakeTele = 22.0;
    }

    @Config
    public static class Turret {
        public static double kP = 0.001;
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
