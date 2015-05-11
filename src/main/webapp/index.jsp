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
 </head>

 <body>
  <h1>えきから to OuDia</h1>

    <!-- <form action="http://walt.dix.asia:9192/jaxrs/ekikara2oudia/getOuDia" method="post"> -->
    <form action="jaxrs/ekikara2oudia/getOuDia" method="post">
      線区番号: <input class="textfield" type="text" name="lineNumber" maxlength="7" value='<%=request.getParameter("lineNumber") == null ? "" : request.getParameter("lineNumber")%>'>
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

  <p>
    幾人かの方、errorのご指摘ありがとうございました。原因究明中です。手元では動くので、GAEの問題なんですが... 取り敢えずうちのserverのを提供します。(2015.1.31) → 何か、動いてるようなので、GAE上のもので再開します。(2015.2.4)
  </p>
  <p>
    京急本線のような大規模な路線については、60秒のtimeout制限を越えてしまうことがあるようです。失敗した後、30秒以内に同じ条件で再試行すれば、cacheが効いて成功するようです。(2014.12.16) → multithread化しました。少しは改善したようです。(2014.12.16)
  </p>
  <p>
    要望を受け、「(料金不要)」を除去し京急快特等を認識するようにしました。(2014.12.14)
  </p>
  <p>
    旧版は廃止しました。(2014)
    <!-- <a href="http://mickey.homelinux.net/~u-ryo/java/ekikara2oud/index.html">旧版</a>を用意しました。もしどうしてもうまく動かない場合には、旧版を使って下さい。 -->
    <!-- 但し、2012年4月下旬に一旦止まる予定です。-->
  </p>
  <p>GAEでは、毎日JSTで17:00にquota resetされます。</p>
  <p>Bookmarklet:
    <a id="result_anchor" href="javascript:(function(){var lineNumber=location.href.replace(/http:\/\/ekikara.jp\/newdata\/[a-z]+\/(\d+)[/.].*/,'$1');if(isNaN(lineNumber)){alert('Here is not ekikara.jp.');}else{location.href='https://ekikara2oudia.appspot.com/?lineNumber='+lineNumber;}})();">Go to Ekikara2OuDia</a>
    <!-- <a id="result_anchor" href="javascript:(function(){var lineNumber=location.href.replace(/http:\/\/ekikara.jp\/newdata\/[a-z]+\/(\d+)[/.].*/,'$1');if(isNaN(lineNumber)){alert('Here is not ekikara.jp.');return;}var args='lineNumber='+lineNumber+'&day=&processTables=1&startTime=0400';var xmlHttpRequest=new XMLHttpRequest();xmlHttpRequest.onreadystatechange=function(){var READYSTATE_COMPLETED=4;var HTTP_STATUS_OK=200;if(this.readyState==READYSTATE_COMPLETED&&this.status==HTTP_STATUS_OK){location.href='data:application/octet-stream,'+encodeURIComponent(this.responseText);}};xmlHttpRequest.open('POST','https://ekikara2oudia.appspot.com/jaxrs/ekikara2oudia/getOuDia');xmlHttpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');xmlHttpRequest.send(args);})();">Create OuDia Weekday</a> -->
    (えきから時刻表の「線区番号」が出ているページ、即ち「路線時刻表」「路線沿線(駅名)」「列車詳細」で有効です。今どきUTF-8でなくShift-JISでattachement fileをajaxから落とすのが難しいのと、どうせparameter調整が必要だろうということで、単に遷移するだけにしました)
  </p>
  <!-- p>列車番号がない! 神戸電鉄等に対応してみました。(2012.1.10)</p -->

  <!-- h3>IEで文字化けする場合</h3 -->
  <!-- p --><!-- こちらのheaderがおかしいんじゃありません。落ち着いて、 --><!-- 大した手間ではないのでmeta tagも入れましたが、IEの「表示(<span class="underline">V</span>)」→「エンコード(<span class="underline">D</span>)」→一番上の「自動選択」にチェックを入れましょう。...というか、IEではdefaultでそうなってないこと、更にそもそもIEを使うことの方に疑念を持つべきかと。</p -->

<!--   <h3>DiaaiD</h3><p><a href="http://saas.mobitan.org/diaaid/">DiaaiD</a>(どこなびドットコム の時刻表データを WinDIA ファイルに変換するサービス)というのが出来たらしいです。今後の発展を期待したいですね。</p> -->

  <!-- h3>getekikara</h3><p><a href="http://code.google.com/p/getekikara/">getekikara</a>(えきから時刻表 のdataを、同一路線の複数線区を合成し、着時刻を含めてoudファイルに変換するpython script)というのが出来つつあるらしいです。いいのが出来たら乗り換えたいですね。</p -->

  <!-- h3>OhatonoDia</h3><p><a href="https://skydrive.live.com/redir.aspx?cid=9b7721af5f35d440&resid=9B7721AF5F35D440!672">のぞみ１９号さん作のOuDia用データライブラリ</a>が見付かりましたので、ご紹介します。複数の線を繋げたデータ作成の方をされて行くとのことですので、活用されればと思います。</p -->

<!--   <h3>喧伝: <a href="../JRHokkaido_Shiritori/">JR北海道しりとり合戦</a></h3> -->

  <!-- h3>2011.10.17のえきから時刻表の突然の変更に対応</h3 -->
  <!-- p>何か、えきから時刻表のtable構成が突如変わったようですね10/17に何の予告もなく。まぁ、勝手にparseしてるだけなので文句は言えませんが。例によって、programはparseするtableの段を適当にずらすだけで行けましたけど。</p --><!-- p>たださぁ、動かなくなっちゃったの見付けたなら、便所の落書きやmixiのコミュニティトピなんかに書き込むんじゃなくって、mailで作者に言えよなぁおまいら、という感じです。作者だって、毎日こんなの使ってるわけではないのです。</p -->

  <!-- <p>初めて直接要望がありました。取り敢えず、まず「1.02」にしました(2/9/2014)。「列車種別の変換を抑制するオプション」は、難しいです。というのも、「列車種別」は任意文字列ではなく、dia fileの冒頭で全種類列挙しておく必要があるからです。「快速急行」の他、変換して欲しい列車種別を挙げて下さい(私鉄には列車種別が多すぎて、私が調査する気にはなりません)。もしくは、任意文字列を書けるような仕様にするようOuDiaの作者に働きかけて下さい(dia fileが大幅に変わってしまいそうですが)。それからついでに、「鹿児島本線で運転日注意の休日運休列車も変換されてしまう」のは、具体的にはよくわかりませんが、そもそもえきから時刻表の休日ページに休日運休列車が掲載されている方がおかしいんじゃないんですか?(2/23/2014)</p> -->

  <h3>これは何?</h3>
  <p>『<a href="http://ekikara.jp/">えきから時刻表</a>』のdataを、<a href="http://homepage2.nifty.com/take-okm/oudia/">OuDia</a>形式のfileに変換するJava programとその<!-- cgi wrapper -->jaxrs programです。</p>

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

  <!-- p>その他、バージョン番号もparameterizeしてありますが、バージョン番号はユーザがいじる必要を特に感じないので、このcgiでのoption入力は省略しています。</p -->

  <h3>注意事項</h3>
  <!-- p>現在(=2008.2.5)、鶴見線の出力が空なのは、えきから時刻表の方がおかしいためです。</p -->
  <p class="important">加工データは、あくまでも個人的な利用に限って下さい。</p>
<!--   <p class="important">OuDia version 0.06(以上)用に変更しました。OuDiaのversion upをして下さい。</p> -->
  <p>尚、defaultで列車種別「普通」<!-- 「快速」「区間快速」「新快速」「特別快速」 -->は「停車駅明示をしない」にしてあります。</p>
  <p>微調整(全列車通過する駅の扱い等)は、OuDia上で行なうとよいでしょう。これは、叩き台として使うのが良いと思います。<!-- → versionが上がって、0.06ではfile formatも変わっていますが、後方(下位)互換性はあるようですので、取り敢えずこのままにしておいてみます。 --></p>

  <h3>感想</h3>
  <p>私は、1年弱程個人的にこのconverterを使って来ましたが、とても便利だと思っています。余りにも便利なので、この余慶を皆様にもお裾分けしようと、この<!-- cgi -->jaxrsを作りました。</p>
  <p>確かに、2007年度開始時に『えきから時刻表』のformatが変わり、一時的に使えなくなる、ということがありました。が、よく観察すると大した変更ではなく、parseするtableを一段ずらすだけの変更で間に合いました。『えきから時刻表』は、mash up出来るように賢く出来てはいないので、追随するのは躊躇われるかもしれませんけれども、その欠点を補って余りある便利さだと感じています。上下700本以上ある山手線(1301041)のダイヤも、一発で出来てしまいます。</p>
  <!-- えきから、2008.10.19にminor version upしくさった。何も告知なしで。一瞬、うち用対策かと焦ったが、なので一応user agentはlibwww-perlをよすようにしたが、そうではなかったっぽい。鹿児島本線新八代や七尾線和倉温泉の「発・発」というbugが直ってたし、バルーンさがも「(0959)」とか括弧つきで時刻入ってたし、少しだけ改良した模様。でもおかげで、「down1.htm」から「down1_1.htm」とURLも無駄に変わってたし、「更新日」の位置がぶれる(違うtrにある場合がある)ようになってた。ともあれ、cgiのlevelとJavaのlevelの両方で、動かなくなってた。 -->

  <h3><a href="ekikara2oudia-1.2.9.tar.bz2">source code</a></h3>
  <p>このconverterはJava(ver. 1.7以上)で作られています。maven2でbuildして下さい。</p>

  <h3>Acknowledgement</h3>
  <p>OuDiaという便利なsoftwareを作って下さったtake-okm氏に感謝します。</p>
  <p>これはconverterですので、あくまでOuDia本体あってこそ、ですし。</p>


  <h3>自分で動かすには</h3>
  <p><!-- <a href="http://jericho.htmlparser.net/">jericho-html</a>と<a href="http://commons.apache.org/logging/">commons-logging</a>を、それぞれ「jericho-html.jar」と「commons-logging.jar」という名前にして、ekikara2oudia-1.2.8.jarと同じdirectoryに置いて下さい。 -->引数には、えきから時刻表のURLを取ります。実行はcommand lineにて、例えば、以下のように打ちます。</p>
  <blockquote><kbd>java -Dfile.encoding=sjis [-DprocessTables=1] [-DKitenJikoku=300] -jar ekikara2oudia-1.2.9.one-jar.jar http://ekikara.jp/newdata/line/2701241/down1_1_holi.htm http://ekikara.jp/newdata/line/2701241/up1_1_holi.htm</kbd></blockquote>
  <p>maven2でのbuildの仕方は、<a href="http://www.nulab.co.jp/kousei/chapter4/01.html">Maven2によるビルド入門</a>等で勉強して下さい(こういう説明をするのが面倒だったので、web applicationにしたのです)。</p>


  <hr/>
  <address><a href="mailto:u-ryo＠walt.dix.asia">Ryo UMETSU</a>(since 2008.2.8)</address>
  <!-- hhmts start -->Last modified: Thu Dec 14 01:51:37 JST 2014 <!-- hhmts end -->

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-15902835-1");
pageTracker._trackPageview();
} catch(err) {}</script>

 </body>
</html>
