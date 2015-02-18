<?php
if(isset($_POST['arguments']) && !empty($_POST['arguments'])) {
        $host = "tcp://localhost"; 
        $port = 6789;
        $data = 'return '.json_encode($_POST['arguments']).PHP_EOL;  //Adding PHP_EOL was the other part of the solution
        $errstr = '';
        $errno = '';

        if ( ($fp = fsockopen($host, $port, $errno, $errstr, 3) ) === FALSE)
            $obs = array('Failed to connect to database');
        else {
            fwrite($fp, $data);
            while (! feof($fp)) {
                $obs = json_decode(fgets($fp, 4096));
            }
            fclose($fp);
        }
    }
?>