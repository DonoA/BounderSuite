<?php
class Dev_PageCallback_OBFormPHP{
    
    public static function getHTML($param){
        if($param != 'new'){
            $host = "tcp://localhost"; 
            $port = 6789;
            $data = 'fetch '.$param.PHP_EOL;  //Adding PHP_EOL was the other part of the solution
            $errstr = '';
            $errno = '';

            if ( ($fp = fsockopen($host, $port, $errno, $errstr, 3) ) === FALSE)
                $obdat = array();
            else {
                fwrite($fp, $data);
                while (! feof($fp)) {
                    $data = fgets($fp, 4096);
                    $obdat = json_decode($data);
                }
                fclose($fp);
            }
            $bginfo = "";
            if($obdat->obname == null){
                $bginfo = "Name: Unknown <br>";
            }else{
                $bginfo = "Name: ".$obdat->obname." <br>";
            }
            $bginfo = $bginfo."UUID: ".$obdat->obuuid." <br>";
            $bginfo = $bginfo."Date Demoted: ".$obdat->demotion." <br>";
            $bginfo = $bginfo."Destination: ".$obdat->destination->name." <br>";
            if($obdat->started){
                $bginfo = $bginfo."Started: True ";
            }else{
                $bginfo = $bginfo."Started: False ";   
            }
            $newDiv = '<p>'.$bginfo.'<br><hr><h1>Banned On:</h1><br><input type=&quot;checkbox&quot; id=&quot;B0&quot;/> Build<br><br><input type=&quot;checkbox&quot; id=&quot;B1&quot;/> Freebuild<br><br><input type=&quot;checkbox&quot; id=&quot;B2&quot;/> TeamSpeak<br><br><input type=&quot;checkbox&quot; id=&quot;B3&quot;/> Forum<a>(Not Automatic)</a><br><br><input type=&quot;checkbox&quot; id=&quot;B4&quot;/> Other: <input type=&quot;text&quot; id=&quot;B5&quot;/><p></p><br><br><input type=&quot;checkbox&quot; id=&quot;BAuto&quot;> Auto Complete<p></p><br><hr><h1>Reasons:</h1> <br><br><input type=&quot;checkbox&quot; id=&quot;R0&quot;/> Unauthorised Block Break/Place <br><br><input type=&quot;checkbox&quot; id=&quot;R1&quot;/> Spamming Chat <br><br><input type=&quot;checkbox&quot; id=&quot;R2&quot;/> Social Indiscretion (Inappropriate Username, Profane/Derogatory Language, Political/Religious Discussions, Referencing Vulgar/Explicit Material) <br><br><input type=&quot;checkbox&quot; id=&quot;R3&quot;/> Ignoring Staff Direction / Impeding Work <br><br><input type=&quot;checkbox&quot; id=&quot;R4&quot;/> Impersonating Staff Member <br><br><input type=&quot;checkbox&quot; id=&quot;R5&quot;/> Use of Detrimental 3rd Party Mods <br><br><input type=&quot;checkbox&quot; id=&quot;R6&quot;/> Advertising <br><br><input type=&quot;checkbox&quot; id=&quot;R7&quot;/> Assisting Oathbreakers <br><br><input type=&quot;checkbox&quot; id=&quot;R8&quot;/> TeamSpeak (Infractions) <br><br><input type=&quot;checkbox&quot; id=&quot;R9&quot;/> Alt Account <br><br><input type=&quot;checkbox&quot; id=&quot;R10&quot;/> Assisting Oathbreakers <br><br><input type=&quot;checkbox&quot; id=&quot;R11&quot;/>Other: <input id=&quot;R12&quot;/> <p></p> <br><br><input type=&quot;checkbox&quot; id=&quot;RAuto&quot;/> Auto Collect Evidence<p></p><br><br><hr><h1>Evidence:</h1><br><input type=&quot;text&quot; id=&quot;evidence&quot;><p></p><br><hr><h1>Notes:</h1><br><textarea rows=&quot;7&quot; cols=&quot;100&quot; id=&quot;notes&quot; style=&quot;width:500px;height:200px&quot;></textarea><p></p><br><button type=&quot;button&quot; onclick=&quot;Update()&quot;>Update</button>';
            $newDiv = str_replace("<","&lt;",$newDiv);
            $newDiv = str_replace(">","&gt;",$newDiv);
            $newDiv = str_replace('"',"&quot;",$newDiv);
            return "document.getElementById('MainDiv').innerHTML = '".$newDiv."'";
        }else{
               $newDiv = '<h1>Name: </h1><input type=&quot;text&quot; id=&quot;name&quot; /><br><br><br> <h1>UUID: </h1><input type=&quot;text&quot; id=&quot;uuid&quot; /><br><br><br> <h1>Date Demoted: </h1><input type=&quot;text&quot; id=&quot;demoteDate&quot; /><br><br><br> <h1>Started: </h1><input type=&quot;date&quot; id=&quot;started&quot; /><br><br><hr><br><br> <h1>Enforcer: </h1> <br><input type=&quot;radio&quot; name=&quot;E&quot; value=&quot;Credoo&quot; />Credoo<br> <br><input type=&quot;radio&quot; name=&quot;E&quot; value=&quot;Dynodaring64&quot; />Dynodaring64<br> <br><input type=&quot;radio&quot; name=&quot;E&quot; value=&quot;Ghundra&quot; />Ghundra<br> <br><input type=&quot;radio&quot; name=&quot;E&quot; value=&quot;Iru_&quot; />Iru_<br> <br><input type=&quot;radio&quot; name=&quot;E&quot; value=&quot;SugarKoala&quot; />SugarKoala<br> <br><input type=&quot;radio&quot; name=&quot;E&quot; value=&quot;q220&quot; />q220<br> <br><input type=&quot;radio&quot; name=&quot;E&quot; value=&quot;Shenjtor&quot; />Shenjtor<br> <br><input type=&quot;radio&quot; name=&quot;E&quot; value=&quot;MaDIIRed&quot; />MaDIIRed<br><br><br> <br><hr><h1>Banned On:</h1><br><input type=&quot;checkbox&quot; id=&quot;B0&quot;/> Build<br><br><input type=&quot;checkbox&quot; id=&quot;B1&quot;/> Freebuild<br><br><input type=&quot;checkbox&quot; id=&quot;B2&quot;/> TeamSpeak<br><br><input type=&quot;checkbox&quot; id=&quot;B3&quot;/> Forum<a>(Not Automatic)</a><br><br><input type=&quot;checkbox&quot; id=&quot;B4&quot;/> Other: <input type=&quot;text&quot; id=&quot;B5&quot;/><p></p><br><br><input type=&quot;checkbox&quot; id=&quot;BAuto&quot;> Auto Complete<p></p><br><hr><h1>Reasons:</h1> <br><br><input type=&quot;checkbox&quot; id=&quot;R0&quot;/> Unauthorised Block Break/Place <br><br><input type=&quot;checkbox&quot; id=&quot;R1&quot;/> Spamming Chat <br><br><input type=&quot;checkbox&quot; id=&quot;R2&quot;/> Social Indiscretion (Inappropriate Username, Profane/Derogatory Language, Political/Religious Discussions, Referencing Vulgar/Explicit Material) <br><br><input type=&quot;checkbox&quot; id=&quot;R3&quot;/> Ignoring Staff Direction / Impeding Work <br><br><input type=&quot;checkbox&quot; id=&quot;R4&quot;/> Impersonating Staff Member <br><br><input type=&quot;checkbox&quot; id=&quot;R5&quot;/> Use of Detrimental 3rd Party Mods <br><br><input type=&quot;checkbox&quot; id=&quot;R6&quot;/> Advertising <br><br><input type=&quot;checkbox&quot; id=&quot;R7&quot;/> Assisting Oathbreakers <br><br><input type=&quot;checkbox&quot; id=&quot;R8&quot;/> TeamSpeak (Infractions) <br><br><input type=&quot;checkbox&quot; id=&quot;R9&quot;/> Alt Account <br><br><input type=&quot;checkbox&quot; id=&quot;R10&quot;/> Assisting Oathbreakers <br><br><input type=&quot;checkbox&quot; id=&quot;R11&quot;/>Other: <input id=&quot;R12&quot;/> <p></p> <br><br><input type=&quot;checkbox&quot; id=&quot;RAuto&quot;/> Auto Collect Evidence<p></p><br><br><hr><h1>Evidence:</h1><br><input type=&quot;text&quot; id=&quot;evidence&quot;><p></p><br><hr><h1>Notes:</h1><br><textarea rows=&quot;7&quot; cols=&quot;100&quot; id=&quot;notes&quot; style=&quot;width:500px;height:200px&quot;></textarea><p></p><br><button type=&quot;button&quot; onclick=&quot;Update()&quot;>Update</button>';
            return "document.getElementById('MainDiv').innerHTML = '".$newDiv."'";
        }
    }
}
?>