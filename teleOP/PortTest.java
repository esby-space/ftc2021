package org.firstinspires.ftc.teamcode.teleOP;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "PORTS", group = "TeleOP")
public class PortTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        // MOTORS AND SERVOS //
        DcMotor frontLeft = hardwareMap.dcMotor.get("leftFront");
        DcMotor frontRight = hardwareMap.dcMotor.get("rightFront");
        DcMotor backLeft = hardwareMap.dcMotor.get("leftRear");
        DcMotor backRight = hardwareMap.dcMotor.get("rightRear");

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.a) {
                frontLeft.setPower(1);
            } else if (gamepad1.b) {
                frontRight.setPower(1);
            } else if (gamepad1.x) {
                backLeft.setPower(1);
            } else if (gamepad1.y) {
                backRight.setPower(1);
            } else {
                frontLeft.setPower(0);
                frontRight.setPower(0);
                backLeft.setPower(0);
                backRight.setPower(0);
            }

            idle();
        }
    }
}

// 14413!

// /\__/\
// (=o.o=)
// |/--\|
// (")-(")
