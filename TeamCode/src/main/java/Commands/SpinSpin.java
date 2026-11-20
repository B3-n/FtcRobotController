package Commands;

import Subsystems.Turret;
import Utils.Constants;

import com.arcrobotics.ftclib.command.CommandBase;

public class SpinSpin extends CommandBase {
    private final Turret m_turret;

    public SpinSpin(Turret turret) {
        m_turret = turret;
        addRequirements(turret);
    }

    @Override
    public void execute() {
        m_turret.runFlywheel(Constants.Speeds.flyWheelSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        m_turret.stopFlywheel();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
