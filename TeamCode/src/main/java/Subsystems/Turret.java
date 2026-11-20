package Subsystems;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import Utils.PIDController;

public class Turret extends SubsystemBase {
    Motor Flywheel;

    HardwareMap m_hardwareMap;

    public Turret(HardwareMap hardwareMap) {
        m_hardwareMap = hardwareMap;
    }

    public void initFlywheel() {
        Flywheel = new Motor(m_hardwareMap, "Flywheel", Motor.GoBILDA.RPM_312);
    }

    PIDController flySpeed = new PIDController(0.0, 0.0, 0.0);

    double flyS;

    public void runFlywheel(double desired) {
        flyS = flySpeed.calculate(desired, Flywheel.getCorrectedVelocity(), new ElapsedTime());
        Flywheel.set(flyS);
    }

    public void stopFlywheel() {
        Flywheel.stopMotor();
    }
}

