```shell
{
  patients: {
    cardinality: 800000,
    fields: [
      {
        category: Integer,
        length: 4,
        name: subject_id,
        distribution: autoincrement,
        distinct: null,
        mean: null,
        stdv: null,
        min: null,
        max: null
      },
      {
        category: Integer,
        length: 4,
        name: sex,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 1
      },
      {
        category: Integer,
        length: 4,
        name: dob,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: -2000000,
        max: 1500000
      },
      {
        category: Integer,
        length: 4,
        name: dod,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: -2000000,
        max: 1500000
      },
      {
        category: Integer,
        length: 4,
        name: hospital_expire_flg,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 1
      }
    ]
  },
  icustayevents: {
    cardinality: 8000000,
    fields: [
      {
        category: Integer,
        length: 4,
        name: icustay_id,
        distribution: autoincrement,
        distinct: null,
        mean: null,
        stdv: null,
        min: null,
        max: null
      },
      {
        category: Integer,
        length: 4,
        name: subject_id,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 799999
      },
      {
        category: Integer,
        length: 4,
        name: intime,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: -2000000,
        max: 1500000
      },
      {
        category: Integer,
        length: 4,
        name: outtime,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: -2000000,
        max: 1500000
      }
    ]
  },
  chartevents: {
    cardinality: 2400000,
    fields: [
      {
        category: Integer,
        length: 4,
        name: chartevent_id,
        distribution: autoincrement,
        distinct: null,
        mean: null,
        stdv: null,
        min: null,
        max: null
      },
      {
        category: Integer,
        length: 4,
        name: subject_id,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 799999
      },
      {
        category: Integer,
        length: 4,
        name: icustay_id,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 7999999
      },
      {
        category: Integer,
        length: 4,
        name: itemid,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 799999
      },
      {
        category: Integer,
        length: 4,
        name: charttime,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: -2000000,
        max: 1500000
      },
      {
        category: Integer,
        length: 4,
        name: elemid,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 100
      },
      {
        category: String,
        length: 4,
        name: resultstatus,
        distribution: normal,
        distinct: null,
        mean: 7,
        stdv: 3,
        min: null,
        max: null
      },
      {
        category: String,
        length: 4,
        name: annotation,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 200
      }
    ]
  },
  chartitems: {
    cardinality: 800000,
    fields: [
      {
        category: Integer,
        length: 4,
        name: itemid,
        distribution: autoincrement,
        distinct: null,
        mean: null,
        stdv: null,
        min: null,
        max: null
      },
      {
        category: String,
        length: 4,
        name: label,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 5,
        max: 15
      },
      {
        category: String,
        length: 4,
        name: category,
        distribution: delta,
        distinct: null,
        mean: null,
        stdv: null,
        min: 3,
        max: 3
      },
      {
        category: String,
        length: 4,
        name: description,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 200
      }
    ]
  },
  labitems: {
    cardinality: 400000,
    fields: [
      {
        category: Integer,
        length: 4,
        name: itemid,
        distribution: autoincrement,
        distinct: null,
        mean: null,
        stdv: null,
        min: null,
        max: null
      },
      {
        category: String,
        length: 4,
        name: test_name,
        distribution: normal,
        distinct: null,
        mean: 15,
        stdv: 5,
        min: null,
        max: null
      },
      {
        category: String,
        length: 4,
        name: fluid,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 5,
        max: 10
      },
      {
        category: String,
        length: 4,
        name: category,
        distribution: delta,
        distinct: null,
        mean: null,
        stdv: null,
        min: 3,
        max: 3
      },
      {
        category: String,
        length: 4,
        name: description,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 200
      }
    ]
  },
  labevents: {
    cardinality: 1600000,
    fields: [
      {
        category: Integer,
        length: 4,
        name: labevent_id,
        distribution: autoincrement,
        distinct: null,
        mean: null,
        stdv: null,
        min: null,
        max: null
      },
      {
        category: Integer,
        length: 4,
        name: subject_id,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 799999
      },
      {
        category: Integer,
        length: 4,
        name: icustay_id,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 7999999
      },
      {
        category: Integer,
        length: 4,
        name: itemid,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 399999
      },
      {
        category: Integer,
        length: 4,
        name: charttime,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: -2000000,
        max: 1500000
      },
      {
        category: String,
        length: 4,
        name: labvalue,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 10,
        max: 50
      }
    ]
  },
  medevents: {
    cardinality: 2000000,
    fields: [
      {
        category: Integer,
        length: 4,
        name: medevent_id,
        distribution: autoincrement,
        distinct: null,
        mean: null,
        stdv: null,
        min: null,
        max: null
      },
      {
        category: Integer,
        length: 4,
        name: icustay_id,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 7999999
      },
      {
        category: Integer,
        length: 4,
        name: subject_id,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 799999
      },
      {
        category: Integer,
        length: 4,
        name: itemid,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 799999
      },
      {
        category: Integer,
        length: 4,
        name: medduration,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 799999
      },
      {
        category: Integer,
        length: 4,
        name: charttime,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: -2000000,
        max: 1500000
      },
      {
        category: Integer,
        length: 4,
        name: volume,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 10000
      },
      {
        category: Integer,
        length: 4,
        name: dose,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 10000
      },
      {
        category: String,
        length: 4,
        name: solution,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 10,
        max: 20
      },
      {
        category: String,
        length: 4,
        name: route,
        distribution: delta,
        distinct: null,
        mean: null,
        stdv: null,
        min: 3,
        max: 3
      },
      {
        category: String,
        length: 4,
        name: stopped,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 5,
        max: 15
      },
      {
        category: String,
        length: 4,
        name: site,
        distribution: normal,
        distinct: null,
        mean: 10,
        stdv: 3,
        min: null,
        max: null
      },
      {
        category: String,
        length: 4,
        name: annotation,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 200
      }
    ]
  },
  meditems: {
    cardinality: 800000,
    fields: [
      {
        category: Integer,
        length: 4,
        name: itemid,
        distribution: autoincrement,
        distinct: null,
        mean: null,
        stdv: null,
        min: null,
        max: null
      },
      {
        category: String,
        length: 4,
        name: label,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 5,
        max: 15
      },
      {
        category: String,
        length: 4,
        name: category,
        distribution: delta,
        distinct: null,
        mean: null,
        stdv: null,
        min: 3,
        max: 3
      }
    ]
  },
  meddurations: {
    cardinality: 800000,
    fields: [
      {
        category: Integer,
        length: 4,
        name: meddurations_id,
        distribution: autoincrement,
        distinct: null,
        mean: null,
        stdv: null,
        min: null,
        max: null
      },
      {
        category: Integer,
        length: 4,
        name: icustay_id,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 7999999
      },
      {
        category: Integer,
        length: 4,
        name: itemid,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 799999
      },
      {
        category: Integer,
        length: 4,
        name: medevent,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 1999999
      },
      {
        category: Integer,
        length: 4,
        name: starttime,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: -2000000,
        max: 1500000
      },
      {
        category: Integer,
        length: 4,
        name: endtime,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: -2000000,
        max: 1500000
      },
      {
        category: Integer,
        length: 4,
        name: duration,
        distribution: uniform,
        distinct: null,
        mean: null,
        stdv: null,
        min: 0,
        max: 100000
      }
    ]
  }
}
```
