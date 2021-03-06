set tolerate.dataerror.write=tolerate;
set tolerate.dataerror.readext=tolerate;
set tolerate.numsplitechar.lessthan.numberfields.exread=tolerate;

drop table dest_j1;

CREATE TABLE dest_j1(key INT, value STRING, val2 STRING) STORED AS TEXTFILE;

EXPLAIN
INSERT OVERWRITE TABLE dest_j1 
SELECT /*+ MAPJOIN(x) */ x.key, x.value, y.value
FROM src1 x JOIN src y ON (x.value = y.value);

INSERT OVERWRITE TABLE dest_j1 
SELECT /*+ MAPJOIN(x) */ x.key, x.value, y.value
FROM src1 x JOIN src y ON (x.value = y.value);

select * from dest_j1 x order by x.key, x.value;

drop table dest_j1;

