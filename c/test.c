// 構文解析＆意味解析テスト
//      if, while, input, outputの文法が自分のと異なる場合は、
//      自分の文法に合うように直して使うこと。

int a, i;
const int b=2;

if (true) {     // true を 3 に変えてエラーになることを確認
   a=1;
} else {        // { を消したらエラーになることを確認
   a=2;
}

if (false) {    // ( と ) をそれぞれ単独で消したときエラーになることを確認
   a=3;
}

if (a == 3) {
   a=0;
} else if (a < 4) {     // キーワードelseifを導入した人は、そのように直すこと
   a=1;
} else {
   a=2;
}
//else {        // コメントを外して、elseが複数あるのは文法エラーになることを確認
//   a=3;
//}

while (true) {          // whileのつづりを間違えてみてエラーになることを確認
   input a;             // ; を消したときにエラーになることを確認
   //input 3;           // コメントを外したら文法エラーになることを確認
   output b;           // コメントを外したら意味解析エラーになることを確認（定数には読み込めない）
}                       // } を消したらエラーになることを確認

do {
    a = 1 * 2;
} while (true);

while (true)
    do
     a = 1;
    while(false);

if (true) if(false) if(true) a = i;