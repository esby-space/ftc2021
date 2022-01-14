package org.firstinspires.ftc.teamcode.vuforiaOP;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

@TeleOp(name = "TensorFlow Object Detection OP", group = "Vuforia")
public class TensorFlowOP extends LinearOpMode {
    /**
     * TensorFlow model that recognized all objects, specialized ones available
     */
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };

    private static final String VUFORIA_KEY =
            "SEE DISCORD";

    private TFObjectDetector TFOD; // TensorFlow Object Detections
    private VuforiaLocalizer Vuforia; // Used by TFOD to interact with cameras

    @Override
    public void runOpMode() {
        // INITIALIZE VUFORIA AND TENSORFLOW //
        initVuforia();
        initTFOD();

        if (TFOD != null) {
            TFOD.activate();
            // adjust accordingly
            TFOD.setZoom(2.5, 16.0 / 9.0);
        }

        // START //
        telemetry.addData("Say", "Hello, World!");
        telemetry.update();
        waitForStart();

        // MAIN LOOP //
        while (opModeIsActive()) {
            if (TFOD != null) { // if out TFOD exists, see if we recognize anything new
                List<Recognition> updatedRecognitions = TFOD.getUpdatedRecognitions();
                if (updatedRecognitions != null) { // if there are new recognitions:
                    telemetry.addData("# Object Detected", updatedRecognitions.size());

                    // step through the list of recognitions and display boundary info
                    int i = 0;
                    for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                        i++;
                    }
                    telemetry.update();
                }
            }
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        Vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTFOD() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        TFOD = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, Vuforia);
        TFOD.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}
