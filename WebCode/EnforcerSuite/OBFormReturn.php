<?php
    if(isset($_POST['arguments']) && !empty($_POST['arguments'])) {
		if($_POST['arguments']['type'] == "fetch"){
			$param = $_POST['arguments']['param'];
			$host = "tcp://localhost";
			$port = 6789;
			if($param == 'new'){
				$newDiv = 'Name: <input type=text id=name /><br/><br/> '.
                  'UUID: <input type=text id=uuid /><br/><br/> '.
                  'Date Demoted: <input type=date id=demoteDate /><br><br>'.
                  'Type: <select id=class><option>ban</option><option>OB</option></select><br><br>'.
                  'Severity: <select id=sever><option>1st/appealable</option><option>2nd/permanent</option></select>'.
                  '<br><br><hr> Enforcer:  <br><br> <select id=E0>';
        foreach(['MaDIIReD', 'Kulmo', 'q220'] as $enforcer){
          $newDiv = $newDiv.'<option value="'.$enforcer.'">'.$enforcer.'</option>';
        }
        $newDiv = $newDiv.'</select><br>'.
                  '<br><hr><h1>Banned On:</h1><br>'.
                  '<input type=checkbox id=B0 /> Build<br><br>'.
                  '<input type=checkbox id=B1 /> Freebuild<br><br>'.
                  '<input type=checkbox id=B2 /> TeamSpeak<br><br>'.
                  '<input type=checkbox id=B3 /> Forum<a>(Not Automatic)</a><br><br>'.
                  '<input type=checkbox id=B4 /> Other: '.
                  '<input type=text id=B5 /><p></p><br><br>'.
                  '<input type=checkbox id=BAuto > Auto Complete<p></p><br>'.
                  '<hr><h1>Reasons:</h1> <br><br>'.
                  '<input type=checkbox id=R0 /> Unauthorised Block Break/Place <br><br>'.
                  '<input type=checkbox id=R1 /> Spamming Chat <br><br>'.
                  '<input type=checkbox id=R2 /> Social Indiscretion (Inappropriate Username, Profane/Derogatory Language, Political/Religious Discussions, Referencing Vulgar/Explicit Material)'.
                  '<br><br>'.
                  '<input type=checkbox id=R3 /> Ignoring Staff Direction / Impeding Work <br><br>'.
                  '<input type=checkbox id=R4 /> Impersonating Staff Member <br><br>'.
                  '<input type=checkbox id=R5 /> Use of Detrimental 3rd Party Mods <br><br>'.
                  '<input type=checkbox id=R6 /> Advertising <br><br>'.
                  '<input type=checkbox id=R7 /> Assisting Oathbreakers <br><br>'.
                  '<input type=checkbox id=R8 /> TeamSpeak (Infractions) <br><br>'.
                  '<input type=checkbox id=R9 /> Alt Account <br><br>'.
                  '<input type=checkbox id=R10 />Other: '.
                  '<input id=R11 /> <p></p> <br><br>'.
                  '<hr><h1>Evidence:</h1><br>'.
                  '<div id="evidence">';
        $newDiv = $newDiv.'</div>'.
                  '<button onClick="$(\'#evidence\').append(\'<input type=text placeholder=Evidence><br/><br/>\');">Add Field</button>'.
                  '<br><br><input type=checkbox id=RAuto /> Auto Collect Evidence<p></p>'.
                  '<br><hr><h1>Notes:</h1><br>'.
                  '<textarea rows=7 cols=100 id=notes style=width:500px;height:200px>'.
                  '</textarea><p></p><br><button type=button onclick=Add(false)>Add</button><br/><br/><button type=button onclick=Add(true)>Complete</button>';
				echo $newDiv;
			}elseif(strpos($param, 'record/-/') !== FALSE){
				$data = 'fetch/-/'.$param.PHP_EOL;  //Adding PHP_EOL was the other part of the solution
				$errstr = '';
				$errno = '';

				if (($fp = fsockopen($host, $port, $errno, $errstr, 3) ) === FALSE)
					$obdat = array();
				else {
					fwrite($fp, $data);
					while (! feof($fp)) {
						$data = fgets($fp, 4096);
						$obdat = json_decode($data);
					}
					fclose($fp);
				}
        $obrec = $obdat->oldInfractions;
        $obcura = (array)$obdat->currentInfraction;
        $obcur = $obdat->currentInfraction;
				$data = str_replace('"',"&quot;",$data);
        $newDiv = "<p>This player has been punished ".count((array) $obrec)." times in the past</p><br/>".
        "<p>This player has gone by the name(s): ".implode(", ", $obdat->names)."</p><br/>".
        "<p>Their UUID is: ".$obdat->ob."</p><br/>".
        "<p>This player ".(empty($obcura)? "does not have a current infraction": "currently has an infration");
        if(!empty($obcura)){
          $newDiv = $newDiv."<hr /> Current infraction:<br><br>";
          if($obcur->ban){
              $newDiv = $newDiv."Ban that was ".($obcur->severity == 1? "appealable": "non-appealable")."<br /><br />";
              $newDiv = $newDiv."It has since ".($obcur->done ? "been": "not been"). " appealed <br /><br />";
              $newDiv = $newDiv."The player was banned for ".implode(", ",$obcur->reasons)."<br /><br />";
          }else{
              $newDiv = $newDiv."This OB was ".($obcur->severity == 1? "first": "second")." degree <br /><br />";
              $newDiv = $newDiv."The player was OBed for ".implode(", ",$obcur->reasons)."<br /><br />";
          }
          $newDiv = $newDiv."The infraction occured on: ".date("Y-m-d H:i:s", $obcur->demotion/1000);
          $newDiv = $newDiv."<br />";
        }

        for($i = count((array) $obrec) -1; $i >= 0; $i--){
            $newDiv = $newDiv."<hr /> Archived infraction <br><br>";
            $rec = $obrec->$i;
            if($rec->ban){
                $newDiv = $newDiv."Ban that was ".($rec->severity == 1? "appealable": "non-appealable")."<br /><br />";
                $newDiv = $newDiv."It has since ".($rec->done ? "been": "not been"). " appealed <br /><br />";
                $newDiv = $newDiv."The player was banned for ".json_encode($rec->reasons)."<br /><br />";
            }else{
                $newDiv = $newDiv."This OB was ".($rec->severity == 1? "first": "second")." degree <br /><br />";
                $newDiv = $newDiv."The player was OBed for ".json_encode($rec->reasons)."<br /><br />";
            }
            $newDiv = $newDiv."The infraction occured on: ".date("Y-m-d H:i:s", $rec->demotion/1000);
            $newDiv = $newDiv."<br />";
        }
				echo $newDiv."<br />";//.$data;
			}else{
				$data = 'fetch/-/'.$param.PHP_EOL;
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
				$bginfo = $bginfo."UUID: <a id=obuuid>".$obdat->obuuid."</a> <br>";
				$bginfo = $bginfo."Date Demoted: ".gmdate("d M Y H:i:s", $obdat->demotion)." <br>";
				$bginfo = $bginfo."Destination: ".$obdat->destination->name." <br>";
				if($obdat->started){
					$bginfo = $bginfo."Started: True <br>";
				}else{
					$bginfo = $bginfo."Started: False <br>";
				}
				if($obdat->ban){
					$bginfo = $bginfo."Ban: True ";
				}else{
					$bginfo = $bginfo."Ban: False ";
				}
        $Ban_Type = "";
        foreach($obdat->bannedOn as $other){
          if(strpos($other,"OTHER_")!==false){
            $Ban_Type = str_replace("OTHER_","",$other);
          }
        }
        $Reason_Type = "";
        foreach($obdat->reasons as $other){
          if(strpos($other,"OTHER_")!==false){
            $Reason_Type = str_replace("OTHER_","",$other);
          }
        }
				$newDiv = '<p>'.$bginfo.'<br><hr><h1>Banned On:</h1><br>'.
                    '<input type="checkbox" id="B0" '.(in_array('build', $obdat->bannedOn) ? 'checked="checked"' : '').'/> Build<br><br>'.
                    '<input type="checkbox" id="B1" '.(in_array('freebuild', $obdat->bannedOn) ? 'checked="checked"' : '').'/> Freebuild<br><br>'.
                    '<input type="checkbox" id="B2" '.(in_array('teamspeak', $obdat->bannedOn) ? 'checked="checked"' : '').'/> TeamSpeak<br><br>'.
                    '<input type="checkbox" id="B3" '.(in_array('freebuild', $obdat->bannedOn) ? 'checked="checked"' : '').'/> Forum<a>(Not Automatic)</a><br><br>'.
                    '<input type="checkbox" id="B4" '.($Ban_Type != "" ? 'checked="checked"' : '').'/> Other: '.
                    '<input type="text" id="B5" '.($Ban_Type != "" ? 'value="'.$Ban_Type.'"' : '').'/><p></p><br><br>'.
                    '<input type="checkbox" id="BAuto"> Auto Complete<p></p><br><hr><h1>Reasons:</h1> <br><br>'.
                    '<input type="checkbox" id="R0" '.(in_array('Unauthorised Block Break/Place', $obdat->reasons) ? 'checked="checked"' : '').'/> Unauthorised Block Break/Place <br><br>'.
                    '<input type="checkbox" id="R1" '.(in_array('Spamming Chat', $obdat->reasons) ? 'checked="checked"' : '').'/> Spamming Chat <br><br>'.
                    '<input type="checkbox" id="R2" '.
                    (in_array('Social Indiscretion (Inappropriate Username, Profane/Derogatory Language, Political/Religious Discussions, Referencing Vulgar/Explicit Material) ', $obdat->reasons) ? 'checked="checked"' : '').
                    '/> Social Indiscretion (Inappropriate Username, Profane/Derogatory Language, Political/Religious Discussions, Referencing Vulgar/Explicit Material) <br><br>'.
                    '<input type="checkbox" id="R3" '.(in_array('Ignoring Staff Direction / Impeding Work', $obdat->reasons) ? 'checked="checked"' : '').'/> Ignoring Staff Direction / Impeding Work <br><br>'.
                    '<input type="checkbox" id="R4" '.(in_array('Impersonating Staff Member', $obdat->reasons) ? 'checked="checked"' : '').'/> Impersonating Staff Member <br><br>'.
                    '<input type="checkbox" id="R5" '.(in_array('Use of Detrimental 3rd Party Mods', $obdat->reasons) ? 'checked="checked"' : '').'/> Use of Detrimental 3rd Party Mods <br><br>'.
                    '<input type="checkbox" id="R6" '.(in_array('Advertising', $obdat->reasons) ? 'checked="checked"' : '').'/> Advertising <br><br>'.
                    '<input type="checkbox" id="R7" '.(in_array('Assisting Oathbreakers', $obdat->reasons) ? 'checked="checked"' : '').'/> Assisting Oathbreakers <br><br>'.
                    '<input type="checkbox" id="R8" '.(in_array('TeamSpeak', $obdat->reasons) ? 'checked="checked"' : '').'/> TeamSpeak (Infractions) <br><br>'.
                    '<input type="checkbox" id="R9" '.(in_array('Alt Account', $obdat->reasons) ? 'checked="checked"' : '').'/> Alt Account <br><br>'.
                    '<input type="checkbox" id="R10" '.($Reason_Type != "" ? 'checked="checked"' : '').'/>Other: '.
                    '<input id="R11" '.($Reason_Type != "" ? 'value="'.$Reason_Type.'"' : '').'/>'.
                    '<br><br><hr><h1>Evidence:</h1><br>'.
                    '<div id="evidence">';
          foreach($obdat->evidence as $evi){
            $newDiv = $newDiv.'<input type="text" value='.$evi.'><br/><br/>';
          }
          $newDiv = $newDiv.'</div>'.
                    '<button onClick="$(\'#evidence\').append(\'<input type=text placeholder=Evidence><br/><br/>\');">Add Field</button>'.
                    '<br/><br/><input type="checkbox" id="RAuto"/> Auto Collect Evidence<p></p><br><hr><h1>Notes:</h1><br>'.
                    '<textarea rows="7" cols="100" id="notes" style="width:500px;height:200px">'.$obdat->notes.'</textarea>'.
                    '<p></p>'.
                    '<br><br><button type=button onclick=Reply(false)>Update</button><br/><br/><button type=button onclick=Reply(true)>Complete</button>';
				echo $newDiv;
			}
		}elseif($_POST['arguments']['type'] == "search"){
			$host = "tcp://localhost";
			$port = 6789;
			$data = 'search/-/'.json_encode($_POST['arguments']).PHP_EOL;  //Adding PHP_EOL was the other part of the solution
			$errstr = '';
			$errno = '';

			if ( ($fp = fsockopen($host, $port, $errno, $errstr, 3) ) === FALSE)
				echo 'Failed to connect to database';
			else {
				fwrite($fp, $data);
				while (! feof($fp)) {
					$results = json_decode(fgets($fp, 4096));
				}
				fclose($fp);
			}
      $newDiv = 'results';
      echo $newDiv;

		}elseif($_POST['arguments']['type'] == "update"){
      $host = "tcp://localhost";
			$port = 6789;
			$data = 'return/-/'.json_encode($_POST['arguments']).PHP_EOL;  //Adding PHP_EOL was the other part of the solution
			$errstr = '';
			$errno = '';

			if ( ($fp = fsockopen($host, $port, $errno, $errstr, 3) ) === FALSE)
				echo 'Failed to connect to database';
			else {
				fwrite($fp, $data);
				while (! feof($fp)) {
					$data = fgets($fp, 4096);
				}
				fclose($fp);
			}
      echo $data;
    }elseif($_POST['arguments']['type'] == "add"){
      $host = "tcp://localhost";
			$port = 6789;
			$data = 'add/-/'.json_encode($_POST['arguments']).PHP_EOL;  //Adding PHP_EOL was the other part of the solution
			$errstr = '';
			$errno = '';

			if ( ($fp = fsockopen($host, $port, $errno, $errstr, 3) ) === FALSE)
				echo 'Failed to connect to database';
			else {
				fwrite($fp, $data);
				while (! feof($fp)) {
					$data = fgets($fp, 4096);
				}
				fclose($fp);
			}
      echo $data;
    }
  }
?>
