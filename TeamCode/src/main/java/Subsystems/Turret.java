package Subsystems;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import Utils.Constants;
import Utils.PIDController;

public class Turret extends SubsystemBase {
    Motor Flywheel;

    HardwareMap m_hardwareMap;

    public static double realVelo;

    private static final double TICKS_PER_REV = 28.0;
    private static final double MAX_RPM = 6000.0;

    public Turret(HardwareMap hardwareMap) {
        m_hardwareMap = hardwareMap;
    }

    public void initFlywheel() {
        Flywheel = new MotorEx(m_hardwareMap, "Flywheel", TICKS_PER_REV, MAX_RPM);
        Flywheel.setRunMode(Motor.RunMode.VelocityControl);
        Flywheel.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        Flywheel.setVeloCoefficients(Constants.Turret.kP, Constants.Turret.kI, Constants.Turret.kD);
    }

    double flyS;

    public void runFlywheel(double desired) {
        flyS = desired / 100;
        Flywheel.set(flyS);
    }

    @Override
    public void periodic() {
        realVelo = Flywheel.getCorrectedVelocity();
    }

    public void stopFlywheel() {
        Flywheel.stopMotor();
    }
}

