package OpModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.button.Trigger;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Commands.SpinSpin;
import Subsystems.Turret;
import Utils.Constants;

@TeleOp(name = "TurretTesting", group = "TeleOp")
public class TurretTesting extends LinearOpMode {
    Turret turret;

    @Override
    public void runOpMode() {

        FtcDashboard dashboard = FtcDashboard.getInstance();

        turret = new Turret(hardwareMap);
        turret.initFlywheel();

        new Trigger(() -> gamepad1.right_trigger > 0.1)
                .whileActiveContinuous(new SpinSpin(turret));

        new Trigger(() -> gamepad1.y)
                .whenActive(new InstantCommand(() -> Constants.Speeds.flyWheelSpeed += 1));

        new Trigger(() -> gamepad1.x)
                .whenActive(new InstantCommand(() -> Constants.Speeds.flyWheelSpeed -= 1));

        waitForStart();

        while (opModeIsActive()) {
            CommandScheduler.getInstance().run();
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("RPS: ", Constants.Speeds.flyWheelSpeed);
            dashboard.sendTelemetryPacket(packet);
        }

        CommandScheduler.getInstance().reset();
    }
}
