<?php
class Dev_PageCallback_OBForm{
    
    public static function respond(XenForo_Controller $controller, XenForo_ControllerResponse_Abstract $response){
        $host = "tcp://localhost"; 
        $port = 6789;
        $data = 'ping' . PHP_EOL;  //Adding PHP_EOL was the other part of the solution
        $errstr = '';
        $errno = '';

        if ( ($fp = fsockopen($host, $port, $errno, $errstr, 3) ) === FALSE){
            $obs = array('fail to connect to database');
        }else{
            fwrite($fp, $data);
            while(! feof($fp)){
                $obs = json_decode(fgets($fp, 4096));
            }
            fclose($fp);
        }
        $response->params['obs'] = $obs;
        $response->templateName = 'OBFormTemplate';
    }
}
?>