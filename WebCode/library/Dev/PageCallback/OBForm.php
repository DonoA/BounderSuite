<?php
class Dev_PageCallback_OBForm{

    public static function respond(XenForo_Controller $controller, XenForo_ControllerResponse_Abstract $response){
        $key = substr(md5(rand()), 0, 7);
        $host = "tcp://localhost";
        $port = 6789;
        $ping = 'ping/-/' . PHP_EOL;  //Adding PHP_EOL was the other part of the solution
        $vald = 'validate/-/12341234/-/' . $key . PHP_EOL;
        $errstr = '';
        $errno = '';
	      $fp = @fsockopen($host, $port, $errno, $errstr, 1);
        if (!$fp){
			       $response->params['up'] = 0;
             $obs = array();
        }else {
			       $response->params['up'] = 1;
             fwrite($fp, $ping);
             while (! feof($fp)) {
                  $obs = json_decode(fgets($fp, 4096));
             }
             fclose($fp);
        }
        $fp = @fsockopen($host, $port, $errno, $errstr, 1);
        if (!$fp){
			       $response->params['up'] = 0;
        }else {
             fwrite($fp, $vald);
             while (! feof($fp)) {
                  $pass = fgets($fp, 4096);
             }
             fclose($fp);
        }
        $response->params['obs'] = $obs;
        $response->params['key'] = $key;
        $response->templateName = 'OBFormTemplate';
    }
}
?>
