<?php
require_once '../util/feedUtils.php';
require_once '../util/templetParse.php';
/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
*/
$cookie_file = "taobao";
$login_action = "http://login.taobao.com/member/login.jhtml";
$data = array("actionForStable"=>"enable_post_user_action","action"=>"Authenticator"
        ,"TPL_username"=>"xxx","TPL_password"=>"xxx","loginType"=>"3","CtrlVersion"=>"1,0,0,7","tid"=>"tid",
        "support"=>"000001","mi_uid"=>"","mcheck"=>"","TPL_redirect_url"=>"www.taobao.com","event_submit_do_login"=>"anything","_oooo_"=>"");
getHtml($login_action,$data,null,$cookie_file);
$url = "http://item.taobao.com/auction/item_detail.htm?xid=0db2&item_num_id=2255050794&cm_cat=50015927&pm2=1&source=dou";
$item_page = getHtml($url,null,null,$cookie_file);
$xml = XmlParse :: html2Xml("../xslt/taobao.xsl", $item_page,false);
$sxml = simplexml_load_string($xml);
$favorite_pop = getHtml($sxml,null,null,$cookie_file);
//$strSrc=array("&nbsp;","&deg;","&rsquo;","&copy;",'&aacute;','&eacute;','&iacute;','&oacute;','&uacute;','&ntilde;');
//$strDes=array(" ","","'","","a","e","i","o","u","n");
$html = cleanHtml($favorite_pop,true);
$html ="<body>".$html."</body>";
$xml = new DOMDocument( );
$xml->loadHTML($html);
$sxml = simplexml_import_dom($xml);
$entry = $sxml->xpath("//form[@id='PopupFavorForm']/script[2]/@src");
$hide_input1 = getHtml($entry[0]->src,null,null,$cookie_file);
$input1 = split("document.write",$hide_input1);
$add_param = array("tags"=>"psp","isShared"=>true,"shopIncluded"=>false);
foreach($input1 as $input){
    $strSrc = array("('<input",">');");
    $strDes = array("","");
    $input  = str_replace($strSrc,$strDes,$input);
    $params = explode(' ', $input);

    foreach ($params as $param){
        $key_value = explode('=', $param);
        if($key_value[0]=="name"){
            $param_name = str_replace("\"", "", $key_value[1]);
        }else if($key_value[0]=="value"){
            $param_value = str_replace("\"", "", $key_value[1]);
            $param_value = str_replace("\r\n", "", $param_value);
        }
    }
    $add_param[$param_name] = $param_value;
}
$entry = $sxml->xpath("//form[@id='PopupFavorForm']/script[1]/@src");
$hide_input2 = getHtml($entry[0]->src,null,null,$cookie_file);
$input2 = split("document.write",$hide_input2);
foreach($input2 as $input){
    $strSrc = array("('<input",">');");
    $strDes = array("","");
    $input  = str_replace($strSrc,$strDes,$input);
    $params = explode(' ', $input);
    foreach ($params as $param){
        $key_value = explode('=', $param);
        if($key_value[0]=="name"){
            $param_name = str_replace("\"", "", $key_value[1]);
        }else if($key_value[0]=="value"){
            $param_value = str_replace("\"", "", $key_value[1]);
            $param_value = str_replace("\r\n", "", $param_value);
        }
    }
    $add_param[$param_name] = $param_value;
}
$page_inputs = $sxml->xpath("//form[@id='PopupFavorForm']/input");
foreach($page_inputs as $input){
    $param_name = (string)$input['name'];
    $param_value = (string)$input['value'];
    $add_param[$param_name] = $param_value;
}
foreach($add_param as $key=>$param){
    echo $key.":".$param;
}
$add_action = "http://favorite.taobao.com/popup/add_collect_success.htm";
echo getHtml($add_action,$add_param,null,$cookie_file);
?>




///////p[@class='action clearfix']/a[2]/@href 这个取收藏页面的xpath
/^collectinfoid=\w+&$/