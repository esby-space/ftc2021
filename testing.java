package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="TestBot: TeleOP", group="TestBot")
public class testing extends LinearOpMode {
    
    @Override
    public void runOpMode() {
        // all of the motors and servos on the robot
        
        DcMotor frontLeftMotor;
        DcMotor frontRightMotor;
        DcMotor backLeftMotor;
        DcMotor backRightMotor;
        DcMotor linearSlide;
        // private DcMotor verticalSweeper;
        DcMotor duckTurner;
        Servo deliverySystem;
        
        // map variables to the actual motors
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");
        frontRightMotor = hardwareMap.dcMotor.get("frontRight");
        backLeftMotor = hardwareMap.dcMotor.get("backLeft");
        backRightMotor = hardwareMap.dcMotor.get("backRight");
        deliverySystem = hardwareMap.servo.get("deliverySystem");
        linearSlide = hardwareMap.dcMotor.get("linearSlide");
        duckTurner = hardwareMap.dcMotor.get("duckTurner");
        
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // make sure it's working
        telemetry.addData("Say", "Hello World!");
        telemetry.update();

        waitForStart();

        // initialize the timer
        ElapsedTime motorTimer = new ElapsedTime();

        // when the start button is pushed, repeat this until end
        while (opModeIsActive()) {

            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;

            // Movement and rotation of the robot with mecanum wheels
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            telemetry.addData("Say", gamepad1.dpad_down);
            telemetry.update();

            // delivery system
            boolean deliverySystemCCW = gamepad1.left_bumper;
            boolean deliverSystemCW = gamepad1.right_bumper;

            if (deliverSystemCW) {
                deliverySystem.setPosition(0.2);
                telemetry.addData("delivery", "clock wise");
                telemetry.update();
            }

            if (deliverySystemCCW) {
                deliverySystem.setPosition(-0.2);
                telemetry.addData("delivery", "counter clock wise");
                telemetry.update();
            }

            // duck wheel
            double duckWheelCCW = gamepad1.left_trigger;
            double duckWheelCW = gamepad1.right_trigger;
            double duckPower;

            if (duckWheelCCW > 0) {
                duckPower = duckWheelCCW;
                duckTurner.setPower(duckPower);
            }

            if (duckWheelCW > 0) {
                duckPower = -duckWheelCW;
                duckTurner.setPower(duckPower);
            }

            // linear slide
            boolean y_button = gamepad1.y;
            boolean x_button = gamepad1.x;
            boolean a_button = gamepad1.a;
            boolean b_button = gamepad1.b;
            double seconds = 0;
            double r = 1; // inches for 1 rotation

            // determine the number of seconds from the bottom the linear slide has to run for
            if (y_button) {
                seconds = 0.431 / r;
            } else if (x_button) {
                seconds = 1.207 / r;
            } else if (a_button) {
                seconds = 2.069 / r;
            } else if (b_button) {
                linearSlide.setPower(-1);
                seconds = 0;
            }

            double startTimer = motorTimer.time();
            while (motorTimer.time() < startTimer + seconds) {
                linearSlide.setPower(1);
            }
            linearSlide.setPower(0);

            idle();
        }
    }
}
