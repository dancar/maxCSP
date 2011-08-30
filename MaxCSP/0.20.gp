reset
set title "Comparing Branch&Bound and PFC-DAC \n P1=0.20"
set terminal postscript portrait size 9,5
set output 'graph0.20.ps'
set key Left bmargin center
set grid x y
set y2tics
set ylabel 'Avg. CCs'
set y2label 'Avg. assignments'
set logscale y
set logscale y2
set xlabel 'P2'
set style line 1 lt 3 lw 1 pt 19 lc rgb 'red'
set style line 2 lt 4 lw 1 pt 8 lc rgb 'red'
set style line 3 lt 1 lw 1 pt 7 lc rgb 'blue'
set style line 4 lt 2 lw 1 pt 9 lc rgb 'blue'
plot 'BranchAndBound_0.20.csv' using 1:2 title 'B&B CCs' with linespoints ls 1, \
'BranchAndBound_0.20.csv' using 1:3 axes x1y2 title 'B&B Assignments' with linespoints ls 2, \
'PFC_DAC_0.20.csv' using 1:2 title 'PFC-DAC CCs' with linespoints ls 3, \
'PFC_DAC_0.20.csv' using 1:3 axes x1y2 title 'PFC-DAC Assignments' with linespoints ls 4 
reset

