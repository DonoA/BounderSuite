<?php
class Dev_PageCallback_OBIndex{
    
    public static function respond(XenForo_Controller $controller, XenForo_ControllerResponse_Abstract $response){
		$response->params['mod'] = XenForo_Visitor::getInstance()->toArray()['is_moderator'];
        $host = "tcp://localhost"; 
        $port = 6789;
        $data = 'fetch/-/archive' . PHP_EOL;
        $errstr = '';
        $errno = '';
		$fp = @fsockopen($host, $port, $errno, $errstr, 1);
        if (!$fp){
			$response->params['up'] = 0;
            $data = array();
        }else {
			$response->params['up'] = 1;
            fwrite($fp, $data);
            while (! feof($fp)) {
                $data = json_decode(fgets($fp, 4096));
            }
            fclose($fp);
        }
        $response->params['obc'] = $data->OBs->Current;
		$response->params['oba'] = $data->OBs->Archived;
		$response->params['banc'] = $data->Bans->Current;
		$response->params['bana'] = $data->Bans->Archived;
//        echo "<script type='text/javascript'>alert('".json_encode($response->params)."');</script>";
        $response->templateName = 'OBIndexTemplate';
    }
}
?>