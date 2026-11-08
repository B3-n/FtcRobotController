package Commands;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.old.CommandOpMode;

public class Telemtry extends CommandOpMode {

    double m_xRobotPosition, m_yRobotPosition, m_heading, m_x, m_y, m_turn, m_leftFront, m_rightFront, m_leftBack, m_rightBack;

    public Telemtry(double xRobotPosition, double yRobotPosition, double heading, double x, double y, double turn, double leftFront, double rightFront, double leftBack, double rightBack) {
        m_xRobotPosition = xRobotPosition;
        m_yRobotPosition = yRobotPosition;
        m_heading = heading;
        m_x = x;
        m_y = y;
        m_turn = turn;
        m_leftFront = leftFront;
        m_rightFront = rightFront;
        m_leftBack = leftBack;
        m_rightBack = rightBack;
    }

    @Override
    public void initialize() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void run() {

    }

    public void telemetryCall() {
        telemetry.addData("Hello: ", 1);
    }
}
