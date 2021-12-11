package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;

@Autonomous(name="auton test")
public class autonTest extends LinearOpMode {

    private DcMotor frontLeftMotor = null;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor linearSlide;
    //  private DcMotor verticalSweeper;
    private DcMotor duckTurner;
    private Servo deliverySystem;
    boolean up = false;
    boolean down =false;
    double elapsedTime;
    private ElapsedTime     runtime = new ElapsedTime();


    @Override public void runOpMode() {
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");
        frontRightMotor = hardwareMap.dcMotor.get("frontRight");
        backLeftMotor = hardwareMap.dcMotor.get("backLeft");
        backRightMotor = hardwareMap.dcMotor.get("backRight");
        linearSlide = hardwareMap.dcMotor.get("linearSlide");
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Say", "auton powered up!");    //
        telemetry.update();

       // frontLeftMotor.setMode(RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(RunMode.RUN_USING_ENCODER);

        frontLeftMotor.setTargetPosition(5);
        waitForStart();
        //driveForwardDistance(.5, 6);
        int ANDYMARK_TICKS_PER_REV;
       // telemetry.addData("Say", frontLeftMotor.getCurrentPosition());    //
       // telemetry.update();

        DriveForward(.5);

        //frontLeftMotor.getCurrentPosition();

       // frontLeftMotor.setTargetPosition(5);

       // frontLeftMotor.isBusy();

    }

    public void driveForwardDistance(double power, int distance){
        if (opModeIsActive()) {

            frontLeftMotor.setMode(RunMode.STOP_AND_RESET_ENCODER);
            frontRightMotor.setMode(RunMode.STOP_AND_RESET_ENCODER);
            backLeftMotor.setMode(RunMode.STOP_AND_RESET_ENCODER);
            backRightMotor.setMode(RunMode.STOP_AND_RESET_ENCODER);

            frontLeftMotor.setTargetPosition(distance);
            frontRightMotor.setTargetPosition(distance);
            backLeftMotor.setTargetPosition(distance);
            backRightMotor.setTargetPosition(distance);

            frontLeftMotor.setMode(RunMode.RUN_TO_POSITION);
            frontRightMotor.setMode(RunMode.RUN_TO_POSITION);
            backLeftMotor.setMode(RunMode.RUN_TO_POSITION);
            backRightMotor.setMode(RunMode.RUN_TO_POSITION);

            runtime.reset();

            DriveForward(power);

            while ((opModeIsActive() &&
                    (runtime.seconds() < 4) &&
                    frontLeftMotor.isBusy() &&
                    frontRightMotor.isBusy() && backRightMotor.isBusy() && backLeftMotor.isBusy())) {

            }

            stopDriving();
            frontLeftMotor.setMode(RunMode.RUN_USING_ENCODER);
            frontRightMotor.setMode(RunMode.RUN_USING_ENCODER);
            backLeftMotor.setMode(RunMode.RUN_USING_ENCODER);
            backRightMotor.setMode(RunMode.RUN_USING_ENCODER);


        }
    }

    public void DriveForward(double power){
        frontLeftMotor.setPower(power);
        backLeftMotor.setPower(power);
        frontRightMotor.setPower(power);
        backRightMotor.setPower(power);
    }
    public void stopDriving(){
        DriveForward(0);
    }
    public void strafeRight(double power){
        frontLeftMotor.setPower(power);
        backLeftMotor.setPower(power * -1);
        frontRightMotor.setPower(power * -1);
        backRightMotor.setPower(power);

    }

    public void strafeLeft(double power){
        frontLeftMotor.setPower(power * -1);
        backLeftMotor.setPower(power);
        frontRightMotor.setPower(power);
        backRightMotor.setPower(power * -1);

    }
}
