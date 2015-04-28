<?php
include_once './DbConnect.php';
function createNewPrediction() {
         $response = array();
        $studentEmail= $_POST["studentEmail"];
        $courseCode= $_POST["courseCode"];
                $db = new DbConnect();
       // mysql query
        $query = "INSERT INTO StudentsTakingCourses(studentEmail,courseCode) VALUES('$studentEmail','$courseCode')";
        $result = mysql_query($query) or die(mysql_error());
        if ($result) {
            $response["error"] = false;
            $response["message"] = "Du har laggts till i gruppen";
        } else {
            $response["error"] = true;
            $response["message"] = "FAIL";
        }
       // echo json response
    echo json_encode($response);
}
createNewPrediction();
?>