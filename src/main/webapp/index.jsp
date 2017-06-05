<%@ page contentType="text/html;charset=utf-8"%>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
 "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xml:lang="ja" dir="ltr" xmlns="http://www.w3.org/1999/xhtml">
 <head>
  <meta name="robots" content="noindex,nofollow,nosnippet,noodp,noydir"/>
  <meta http-equiv="content-type" content="text/html; charset=utf-8">
  <title>えきから to OuDia</title>
  <style type="text/css">
   .line_number { color:red; }
   .important { color:red; font-style:italic; }
   .underline { text-decoration: underline; }
   .textfield { width:68px; }
  </style>
  <script>
   (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
   (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
   m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
   })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

   ga('create', 'UA-70146339-1', 'auto');
   ga('send', 'pageview');
  </script>
 </head>

 <body>
  <h1>えきから to OuDia</h1>

    <form action="jaxrs/ekikara2oudia/getOuDia" method="post">
      線区番号: <input class="textfield" type="text" name="lineNumber" maxlength="7" value='<%=request.getParameter("lineNumber") == null || !request.getParameter("lineNumber").matches("\\d{7}") ? "" : request.getParameter("lineNumber")%>'>
      曜日: <select name="day">
        <option value="">平日</option>
        <option value="_sat">土曜</option>
        <option value="_holi">日祝</option>
      </select>
      処理する表の指定: <input class="textfield" type="text" name="processTables" value="1">
      reverse?: <input type="checkbox" name="reverse" value="true">
      起点時刻: <input class="textfield" type="text" name="startTime" maxlength="4" value="0300">
      <input type="submit">
    </form>

  <h3>What's new</h3>
  <ul>
    <li>
      下記偕楽園の特例、偕楽園が無くなっても大丈夫なように作ったつもりだったんですが、ヌルポが出てました。気付きませなんだ。直しました。テストケース追加しました。知らせて下さった方ありがとうございました。(2017.6.6)
    </li>
    <li>
      えきから時刻表、偕楽園設置時の常磐線は上下線で駅数が違うために、エラーになったり上り列車が1駅ずれたりするので、改良しました。知らせて下さった方、ありがとうございます。<!-- ただ、当然ですが常磐線上り列車は全て偕楽園を通過(平日は下り列車も全て通過)するので、OuDiaでは上り列車が水戸〜赤塚で直立します(<a href="http://kamelong.web.fc2.com/aodia/">AOdia</a>では大丈夫です)。 -->(2017.2.18)<br/>
      (その他、もう作って10年にもなるのに今更ながらUnit Testを追加したりしました)
      (どうも<a href="https://ekikara2oudia.appspot.com/">GAEの方</a>は、一部ページの取得に失敗しやすいようです。<a href="https://ekikara2oudia.herokuapp.com/">heroku版</a>を使って下さい。→一部のページの取得に失敗したらエラーにするようにしました)
    </li>
    <li>
      今更ですが、<a href="http://heroku.com/">heroku</a>を使うことにしました。
      今後は<a href="https://ekikara2oudia.herokuapp.com/">heroku版</a>をmaintenanceしようと思います。
      (2015.11.15)<br/>
      (herokuはGAEより短い30秒制限があるので躊躇ってたんですが、
      GAEだとJava8が使えませんし[Compute Engineは有料]、その他のresources制限が厳しいですし、
      他方herokuを試してみたら京急本線70ファイル取得でも20秒で終わるので
      [去年並列化したのが効いたようです]、
      移行することにしました)
    </li>
  </ul>

  <h3>Bookmarklet:</h3>
  <p>
    <a id="result_anchor" href="javascript:(function(){var lineNumber=location.href.replace(/http:\/\/ekikara.jp\/newdata\/[a-z]+\/(\d+)[/.].*/,'$1');if(isNaN(lineNumber)){alert('Here is not ekikara.jp.');}else{location.href='https://ekikara2oudia.herokuapp.com/?lineNumber='+lineNumber;}})();">Go to Ekikara2OuDia</a>
    (えきから時刻表の「線区番号」が出ているページ、即ち「路線時刻表」「路線沿線(駅名)」「列車詳細」で有効です)
  </p>

  <h3>これは何?</h3>
  <p>『<a href="http://ekikara.jp/">えきから時刻表</a>』のdataを、<a href="http://homepage2.nifty.com/take-okm/oudia/">OuDia</a>形式のfileに変換するJava programとそのjaxrs programです。</p>

  <h3>「線区番号」って?</h3>
  <p>
   『えきから時刻表』である線区の時刻表を表示させ、そのURLを見て下さい。例えば、山田線だと、
   <blockquote>http://ekikara.jp/newdata/line/<span class="line_number">1301691</span>/down1_1.htm</blockquote>
   となります。
  </p>

  <h3>「処理する表」って?(必須option)</h3>
  <p>
   函館本線や中央東線、東北本線、長崎本線や鶴見線のように、1つの線区pageに複数の経由線がある場合、どの経由線のデータを選択するかを指定します。具体的には、例えば<a href="http://ekikara.jp/newdata/line/1301361/down1_1.htm">鶴見線</a>の場合だと、「鶴見→浅野→海芝浦」なら「1,2」と入れ、「鶴見→浅野→安善→武蔵白石→扇町」なら「1,3,5」と入れます。<a href="http://ekikara.jp/newdata/line/4001051/down1_1.htm">長崎本線</a>なら、市布経由の場合は「1,2,4」と入れ、長与経由の場合だと「1,3,4」になります。
  </p>
  <p>通常の場合は、「1」とだけ入力すれば済むでしょう。</p>

  <h3>reverse?</h3>
  <p>例えば東北本線や高崎線、常磐線などで、上野を下にしたい(即ち、上下逆にしたい)場合にcheckして下さい。</p>
  <p>但し、逆にした場合、「処理する表」の順番指定も、上りが基準になるため変わりますので注意して下さい。即ち例えば、<a href="http://ekikara.jp/newdata/line/1301142/down1_1.htm">東北本線(黒磯〜利府・盛岡)</a>で黒磯〜岩切〜利府を表示しようとしてreverseした場合、指定は「1,2」ではなく(上りが基準になるので)「2,3」になります。</p>

  <h3>起点時刻って?</h3>
  <p>列車ダイヤが描画される起点(基点?)となる時刻です。3時とか4時にしておけば、まず大体始発から始まり終電で終わる1日のダイヤが出来ます。</p>
  <p>この値は、OuDia実行後でも変更できますので、そんなにcriticalなoptionではありません。</p>

  <h3>注意事項</h3>
  <p class="important">加工データは、あくまでも個人的な利用に限って下さい。</p>
  <p>尚、defaultで列車種別「普通」<!-- 「快速」「区間快速」「新快速」「特別快速」 -->「バス」は「停車駅明示をしない」にしてあります。</p>
  <p>微調整(全列車通過する駅の扱い等)は、OuDia上で行なうとよいでしょう。これは、叩き台として使うのが良いと思います。</p>

  <h3>感想</h3>
  <p>私は、2007年から個人的にこのconverterを使って来ましたが、とても便利だと思っています。余りにも便利なので、この余慶を皆様にもお裾分けしようと、このjaxrsを作りました。</p>
  <p>確かに、2007年度開始時に『えきから時刻表』のformatが変わり、一時的に使えなくなる、ということがありました。が、よく観察すると大した変更ではなく、parseするtableを一段ずらすだけの変更で間に合いました。『えきから時刻表』は、mash up出来るように賢く出来てはいないので、追随するのは躊躇われるかもしれませんけれども、その欠点を補って余りある便利さだと感じています。上下700本以上ある山手線(1301041)のダイヤも、一発で出来てしまいます。</p>

  <h3><a href="https://github.com/u-ryo/ekikara2oudia/">source code</a></h3>
  <p>このconverterはJava(ver. 1.7以上)で作られています。maven3でbuildして下さい。</p>

  <h3>Acknowledgement</h3>
  <p>OuDiaという便利なsoftwareを作って下さったtake-okm氏に感謝します。</p>
  <p>これはconverterですので、あくまでOuDia本体あってこそ、ですし。</p>


  <h3>自分で動かすには</h3>
  <p>引数には、えきから時刻表のURLを取ります。実行はcommand lineにて、例えば、以下のように打ちます。</p>
  <blockquote><kbd>java -Dfile.encoding=sjis [-DprocessTables=1] [-DKitenJikoku=300] -jar ekikara2oudia-1.2.10.one-jar.jar http://ekikara.jp/newdata/line/2701241/down1_1_holi.htm http://ekikara.jp/newdata/line/2701241/up1_1_holi.htm</kbd></blockquote>
  <p>maven3でのbuildの仕方は、net等で勉強して下さい(こういう説明をするのが面倒だったので、web applicationにしたのです)。</p>

  <hr/>
  <address><a href="mailto:u-ryo＠walt.mydns.bz">Ryo UMETSU</a>(since 2008.2.8)</address>
  <!-- hhmts start -->Last modified: Sat Feb 18 21:16:53 JST 2017 <!-- hhmts end -->

 </body>
</html>
