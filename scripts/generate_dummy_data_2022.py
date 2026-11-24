import hashlib
import random
from datetime import datetime, timedelta

random.seed(2022)

def sha256(s):
    return hashlib.sha256(s.encode('utf-8')).hexdigest()

def escape_sql(val):
    if val is None:
        return "NULL"
    if isinstance(val, (int, float)):
        return str(val)
    s = str(val).replace("\\", "\\\\").replace("'", "''")
    return f"'{s}'"

# 1. Users
users_data = [
    ("admin", "admin123", "Bambang Sudiro, S.T.", "admin@workshop.co.id", "081288991100", "WORKSHOP_SUPERVISOR"),
    ("supervisor_hendra", "admin123", "Hendra Setiawan, M.Eng", "hendra.s@workshop.co.id", "081299882211", "WORKSHOP_SUPERVISOR"),
    ("clerk_budi", "clerk123", "Budi Santoso", "budi.s@workshop.co.id", "081377889922", "INVENTORY_CLERK"),
    ("clerk_siti", "clerk123", "Siti Nurhaliza", "siti.n@workshop.co.id", "081355443322", "INVENTORY_CLERK"),
    ("tech_agus", "tech123", "Agus Supriyatna", "agus.tech@workshop.co.id", "085211223344", "SERVICE_TECHNICIAN"),
    ("tech_eko", "tech123", "Eko Prasetyo", "eko.tech@workshop.co.id", "085222334455", "SERVICE_TECHNICIAN"),
    ("tech_dedi", "tech123", "Dedi Kurniawan", "dedi.tech@workshop.co.id", "085233445566", "SERVICE_TECHNICIAN"),
    ("tech_rudi", "tech123", "Rudi Hartono", "rudi.tech@workshop.co.id", "085244556677", "SERVICE_TECHNICIAN")
]

# 2. Categories
categories_data = [
    ("Engine Components", "Internal combustion engine parts, belts, water pumps, valves, and gaskets"),
    ("Transmission & Drivetrain", "Clutch kits, gearboxes, CV joints, drive shafts, and fluids"),
    ("Brake System", "Brake pads, discs, shoes, master cylinders, and brake fluids"),
    ("Suspension & Steering", "Shock absorbers, tie rods, ball joints, sway bars, and rack ends"),
    ("Electrical & Electronic", "Batteries, alternators, starters, spark plugs, sensors, and bulbs"),
    ("Filters & Air Intake", "Oil filters, air filters, cabin/AC filters, and fuel filters"),
    ("Fluids & Lubricants", "Engine oil, transmission fluid, coolant, power steering, and gear oil"),
    ("Exhaust & Emissions", "Mufflers, catalytic converters, oxygen sensors, and exhaust gaskets"),
    ("Cooling System", "Radiators, thermostats, coolant hoses, radiator caps, and fan assemblies"),
    ("Wheels & Tires", "Tires, wheel bearings, valves, and balance weights"),
    ("Air Conditioning (AC)", "AC compressors, condensers, evaporators, expansion valves, and freon"),
    ("Body, Glass & Accessories", "Wiper blades, horn sets, mirrors, clips, and trim accessories")
]

# 3. Suppliers
suppliers_data = [
    ("SUP-001", "PT Astra Otoparts Tbk", "Iwan Kusuma", "021-4603550", "sales@astra-otoparts.com", "Jl. Raya Pegangsaan Dua Km 2.2, Kelapa Gading", "Jakarta Utara", "Net 30"),
    ("SUP-002", "PT Denso Sales Indonesia", "Maya Susanti", "021-6512300", "order@denso.co.id", "Jl. Gaya Motor I No. 6, Sunter II", "Jakarta Utara", "Net 30"),
    ("SUP-003", "PT Sumber Berkat Motor", "Willy Gunawan", "021-6288812", "info@sumberberkat.com", "Pusat Niaga Dutamas Fatmawati Blok B1/18", "Jakarta Selatan", "Net 14"),
    ("SUP-004", "PT Tri Dharma Kencana", "Ricky Harun", "021-5565123", "procurement@tdk-parts.co.id", "Kawasan Industri Manis, Jl. Manis Raya No. 12", "Tangerang", "Net 30"),
    ("SUP-005", "PT Bando Indonesia", "Suryadi", "021-8934500", "contact@bando.co.id", "Kawasan Industri MM2100 Blok C-3", "Bekasi", "Net 30"),
    ("SUP-006", "PT Gajah Tunggal Tbk", "Linda Wijaya", "021-3863410", "sales@gt-tires.com", "Wisma Hayam Wuruk Lt. 10, Jl. Hayam Wuruk No. 8", "Jakarta Pusat", "Net 45"),
    ("SUP-007", "PT Bridgestone Tire Indonesia", "Denny Pratama", "021-8801122", "customercare@bridgestone.co.id", "Jl. Raya Bekasi Km. 27, Harapan Jaya", "Bekasi", "Net 30"),
    ("SUP-008", "PT NGK Busi Indonesia", "Faisal Basri", "021-8711422", "sales@ngkbusi.com", "Jl. Raya Jakarta-Bogor Km. 26.6, Ciracas", "Jakarta Timur", "Net 30"),
    ("SUP-009", "PT Shell Indonesia", "Citra Kirana", "021-78838888", "orders@shell.co.id", "Talavera Office Park, Jl. TB Simatupang Kav. 22-26", "Jakarta Selatan", "Net 30"),
    ("SUP-010", "PT Pertamina Lubricants", "Agus Salim", "021-31908000", "cs.lubricants@pertamina.com", "Oil Center Building, Jl. MH Thamrin No. 55", "Jakarta Pusat", "Net 30"),
    ("SUP-011", "PT Castrol Indonesia", "Rian Hidayat", "021-52960000", "indonesia.order@castrol.com", "Sentra Senayan II Lt. 16, Jl. Asia Afrika No. 8", "Jakarta Pusat", "Net 30"),
    ("SUP-012", "PT Bosch Indonesia", "Johan Tanuwidjaja", "021-30033000", "aftermarket@bosch.co.id", "Gedung Arkadia Green Park Tower G, TB Simatupang", "Jakarta Selatan", "Net 45"),
    ("SUP-013", "PT Kayaba Indonesia", "Surono", "021-8980123", "sales@kyb.co.id", "Kawasan Industri MM2100 Blok DD-4", "Cikarang Barat", "Net 30"),
    ("SUP-014", "PT Aisin Indonesia Automotive", "Taufik Hidayat", "0267-8631234", "parts@aisin-indonesia.co.id", "Kawasan Industri KIIC Lot LL-9", "Karawang", "Net 30"),
    ("SUP-015", "PT Federal Karyatama", "Wahyu Nugroho", "021-4601234", "sales@fkt.co.id", "Jl. Pulo Buaran Raya Blok III EE Kav. 2-3, Pulogadung", "Jakarta Timur", "Net 30"),
    ("SUP-016", "CV Bintang Jaya Auto Motor", "Tony Sugiarto", "022-4235566", "bintangjaya@gmail.com", "Jl. Banceuy No. 45", "Bandung", "Net 14"),
    ("SUP-017", "PT Mitra Sparepart Nusantara", "Erwin Halim", "031-5345678", "sales@mitrasparepart.co.id", "Jl. Kedungdoro No. 88", "Surabaya", "Net 30"),
    ("SUP-018", "CV Aneka Filter Sejahtera", "Haryono", "024-3567890", "aneka.filter@yahoo.com", "Jl. MT Haryono No. 120", "Semarang", "Net 14")
]

# 4. Spare Parts (105 Realistic Parts)
# (part_number, name, cat_idx (1-based), brand, compatibility, uom, bin, cost, sell, min_stock, initial_stock)
parts_raw = [
    # Engine Components (Cat 1)
    ("PRT-ENG-001", "Timing Belt Kit 1KD/2KD", 1, "Toyota OEM", "Toyota Innova / Fortuner 2.5 Diesel", "SET", "ENG-01-A", 650000, 920000, 5, 18),
    ("PRT-ENG-002", "Fan Belt / Serpentine Belt 4PK1210", 1, "Bando", "Toyota Avanza 1.3 / Xenia 1.3", "PCS", "ENG-01-B", 65000, 95000, 10, 35),
    ("PRT-ENG-003", "Fan Belt / Serpentine Belt 6PK2100", 1, "Bando", "Honda Civic / CR-V 2.0", "PCS", "ENG-01-C", 115000, 165000, 8, 24),
    ("PRT-ENG-004", "Water Pump Engine 1NR/2NR", 1, "Aisin", "Toyota Avanza / Rush / Calya", "PCS", "ENG-02-A", 320000, 460000, 4, 12),
    ("PRT-ENG-005", "Water Pump Engine 2KD-FTV", 1, "Aisin", "Toyota Innova Diesel 2005-2015", "PCS", "ENG-02-B", 480000, 690000, 4, 10),
    ("PRT-ENG-006", "Valve Cover Gasket 1NZ-FE", 1, "Toyota OEM", "Toyota Vios / Yaris 1.5", "PCS", "ENG-03-A", 85000, 125000, 6, 20),
    ("PRT-ENG-007", "Engine Mounting Front Right", 1, "RBI Thailand", "Honda Jazz RS / City GM6", "PCS", "ENG-03-B", 340000, 490000, 4, 14),
    ("PRT-ENG-008", "Engine Mounting Left Transmission", 1, "RBI Thailand", "Honda Jazz RS / City GM6", "PCS", "ENG-03-C", 290000, 420000, 4, 11),
    ("PRT-ENG-009", "Cylinder Head Gasket Metal 2NR", 1, "Toyota OEM", "Toyota Avanza / Sienta 1.5 Dual VVT-i", "PCS", "ENG-04-A", 210000, 310000, 3, 9),
    ("PRT-ENG-010", "Thermostat Assembly 82°C", 1, "Toyota OEM", "Toyota Avanza, Innova, Rush, Fortuner", "PCS", "ENG-04-B", 140000, 210000, 5, 16),

    # Transmission & Drivetrain (Cat 2)
    ("PRT-TRN-001", "Clutch Disc (Plat Kopling) 1.3", 2, "Aisin", "Toyota Avanza 1.3 / Daihatsu Xenia 1.3", "PCS", "TRN-01-A", 280000, 395000, 5, 18),
    ("PRT-TRN-002", "Clutch Cover (Matahari / Dekrup)", 2, "Aisin", "Toyota Avanza 1.3 / Daihatsu Xenia 1.3", "PCS", "TRN-01-B", 310000, 440000, 5, 17),
    ("PRT-TRN-003", "Clutch Release Bearing", 2, "NSK Japan", "Toyota Avanza / Daihatsu Xenia / Terios", "PCS", "TRN-01-C", 125000, 185000, 8, 26),
    ("PRT-TRN-004", "Clutch Master Cylinder Atas", 2, "Sanyco", "Toyota Kijang Innova 2.0/2.5 Bensin/Diesel", "PCS", "TRN-02-A", 240000, 350000, 4, 13),
    ("PRT-TRN-005", "Clutch Slave Cylinder Bawah", 2, "Sanyco", "Toyota Kijang Innova 2.0/2.5 Bensin/Diesel", "PCS", "TRN-02-B", 160000, 235000, 4, 15),
    ("PRT-TRN-006", "Drive Shaft Outer CV Joint Kanan", 2, "NKN Japan", "Honda Brio / Mobilio", "PCS", "TRN-03-A", 380000, 540000, 4, 10),
    ("PRT-TRN-007", "Drive Shaft Outer CV Joint Kiri", 2, "NKN Japan", "Honda Brio / Mobilio", "PCS", "TRN-03-B", 380000, 540000, 4, 10),
    ("PRT-TRN-008", "Automatic Transmission Filter", 2, "Toyota OEM", "Toyota Avanza / Rush AT 4-Speed", "PCS", "TRN-04-A", 175000, 260000, 5, 14),

    # Brake System (Cat 3)
    ("PRT-BRK-001", "Front Brake Pad Ceramic", 3, "Brembo", "Toyota Innova Reborn / Fortuner VRZ", "SET", "BRK-01-A", 550000, 780000, 6, 22),
    ("PRT-BRK-002", "Front Brake Pad Semi-Metallic", 3, "Akebono", "Toyota Avanza / Xenia / Rush 2012-2021", "SET", "BRK-01-B", 185000, 275000, 12, 45),
    ("PRT-BRK-003", "Front Brake Pad Premium", 3, "Nissin OEM", "Honda Jazz GK5 / HR-V / City GM6", "SET", "BRK-01-C", 310000, 445000, 8, 28),
    ("PRT-BRK-004", "Rear Brake Shoe Set", 3, "MK Kashiyama", "Toyota Avanza / Daihatsu Xenia All Years", "SET", "BRK-02-A", 145000, 220000, 10, 36),
    ("PRT-BRK-005", "Rear Brake Shoe Set", 3, "MK Kashiyama", "Honda Brio / Mobilio", "SET", "BRK-02-B", 160000, 240000, 8, 25),
    ("PRT-BRK-006", "Front Brake Disc Rotor Pair", 3, "Brembo", "Toyota Avanza 1.3 / 1.5", "PAIR", "BRK-03-A", 480000, 690000, 4, 14),
    ("PRT-BRK-007", "Front Brake Disc Rotor Pair", 3, "Brembo", "Honda Jazz RS / Honda Brio", "PAIR", "BRK-03-B", 520000, 750000, 4, 12),
    ("PRT-BRK-008", "Rear Wheel Brake Cylinder (Kiri)", 3, "Sanyco", "Toyota Avanza / Daihatsu Xenia", "PCS", "BRK-04-A", 75000, 115000, 8, 22),
    ("PRT-BRK-009", "Rear Wheel Brake Cylinder (Kanan)", 3, "Sanyco", "Toyota Avanza / Daihatsu Xenia", "PCS", "BRK-04-B", 75000, 115000, 8, 22),
    ("PRT-BRK-010", "Brake Fluid DOT 4 1000ml", 3, "Prestone", "Universal All Passenger Cars", "BTL", "BRK-05-A", 62000, 92000, 15, 55),

    # Suspension & Steering (Cat 4)
    ("PRT-SUS-001", "Front Shock Absorber Gas (Kiri)", 4, "Kayaba Excel-G", "Toyota Avanza / Rush / Xenia 2012-2021", "PCS", "SUS-01-A", 450000, 650000, 6, 18),
    ("PRT-SUS-002", "Front Shock Absorber Gas (Kanan)", 4, "Kayaba Excel-G", "Toyota Avanza / Rush / Xenia 2012-2021", "PCS", "SUS-01-B", 450000, 650000, 6, 18),
    ("PRT-SUS-003", "Rear Shock Absorber Gas Pair", 4, "Kayaba Excel-G", "Toyota Avanza / Daihatsu Xenia", "PAIR", "SUS-01-C", 520000, 750000, 6, 20),
    ("PRT-SUS-004", "Front Shock Absorber Heavy Duty (Kiri)", 4, "Kayaba Excel-G", "Toyota Innova Reborn 2016-2022", "PCS", "SUS-02-A", 580000, 830000, 4, 12),
    ("PRT-SUS-005", "Front Shock Absorber Heavy Duty (Kanan)", 4, "Kayaba Excel-G", "Toyota Innova Reborn 2016-2022", "PCS", "SUS-02-B", 580000, 830000, 4, 12),
    ("PRT-SUS-006", "Tie Rod End Outer Pair", 4, "555 Japan", "Toyota Avanza / Xenia / Rush", "PAIR", "SUS-03-A", 220000, 320000, 6, 24),
    ("PRT-SUS-007", "Rack End / Long Tie Rod Pair", 4, "555 Japan", "Toyota Avanza / Xenia / Rush", "PAIR", "SUS-03-B", 260000, 380000, 6, 20),
    ("PRT-SUS-008", "Ball Joint Lower Front Pair", 4, "555 Japan", "Toyota Innova / Fortuner / Hilux", "PAIR", "SUS-04-A", 410000, 590000, 5, 15),
    ("PRT-SUS-009", "Stabilizer Link Rod Front Pair", 4, "555 Japan", "Honda Jazz GK5 / Brio / Mobilio", "PAIR", "SUS-04-B", 210000, 310000, 6, 22),
    ("PRT-SUS-010", "Bushing Arm Lower Besar Pair", 4, "RBI Thailand", "Toyota Avanza / Rush", "PAIR", "SUS-05-A", 130000, 195000, 8, 28),

    # Electrical & Electronic (Cat 5)
    ("PRT-ELC-001", "Car Battery MF 12V 45Ah NS60", 5, "GS Astra MF", "Toyota Avanza, Rush, Daihatsu Terios", "UNIT", "ELC-01-A", 680000, 950000, 6, 19),
    ("PRT-ELC-002", "Car Battery MF 12V 60Ah 55D23L", 5, "GS Astra MF", "Toyota Innova Bensin, Nissan Grand Livina", "UNIT", "ELC-01-B", 890000, 1220000, 5, 14),
    ("PRT-ELC-003", "Car Battery MF 12V 75Ah 80D26L", 5, "GS Astra MF", "Toyota Fortuner / Innova Diesel / Pajero", "UNIT", "ELC-01-C", 1150000, 1580000, 4, 12),
    ("PRT-ELC-004", "Spark Plug Laser Iridium (ILKAR7B11)", 5, "NGK Iridium", "Toyota Avanza Dual VVT-i / Rush / Calya", "PCS", "ELC-02-A", 78000, 115000, 20, 90),
    ("PRT-ELC-005", "Spark Plug Standard Nickel (W16EX-U11)", 5, "Denso", "Toyota Avanza 1.3 VVT-i (2004-2015)", "PCS", "ELC-02-B", 18000, 30000, 25, 120),
    ("PRT-ELC-006", "Spark Plug Iridium Long Life (SK20HR11)", 5, "Denso Iridium", "Toyota Innova Bensin 2.0 / Fortuner 2.7", "PCS", "ELC-02-C", 85000, 125000, 16, 75),
    ("PRT-ELC-007", "Ignition Coil 1NR/2NR Engine", 5, "Denso", "Toyota Avanza Dual VVT-i / Sienta / Yaris", "PCS", "ELC-03-A", 260000, 380000, 6, 20),
    ("PRT-ELC-008", "Alternator Assembly 12V 80A", 5, "Denso", "Toyota Avanza 1.3 / 1.5", "UNIT", "ELC-04-A", 1250000, 1750000, 2, 7),
    ("PRT-ELC-009", "Starter Motor Assembly 12V 1.0kW", 5, "Denso", "Toyota Avanza / Xenia / Rush", "UNIT", "ELC-04-B", 980000, 1380000, 2, 8),
    ("PRT-ELC-010", "Halogen Headlamp Bulb H4 12V 60/55W", 5, "Osram", "Universal Passenger Cars", "PCS", "ELC-05-A", 38000, 60000, 15, 60),
    ("PRT-ELC-011", "Halogen Foglamp Bulb H11 12V 55W", 5, "Osram", "Toyota Innova / Fortuner / Rush", "PCS", "ELC-05-B", 45000, 70000, 12, 40),
    ("PRT-ELC-012", "Horn Set Waterproof Keong 12V", 5, "Denso", "Universal All 12V Vehicles", "SET", "ELC-05-C", 110000, 165000, 8, 28),

    # Filters & Air Intake (Cat 6)
    ("PRT-FLT-001", "Engine Oil Filter (15601-BZ010)", 6, "Denso OEM", "Toyota Avanza, Xenia, Rush, Terios, Calya", "PCS", "FLT-01-A", 22000, 38000, 25, 140),
    ("PRT-FLT-002", "Engine Oil Filter (90915-YZZE1)", 6, "Toyota OEM", "Toyota Innova, Fortuner, Altis, Vios, Yaris", "PCS", "FLT-01-B", 35000, 55000, 20, 110),
    ("PRT-FLT-003", "Engine Oil Filter (15400-RAF-T01)", 6, "Honda OEM", "Honda Brio, Jazz, Mobilio, HR-V, CR-V", "PCS", "FLT-01-C", 38000, 60000, 20, 95),
    ("PRT-FLT-004", "Diesel Fuel Filter Element (23390-0L070)", 6, "Toyota OEM", "Toyota Innova / Fortuner / Hilux Diesel", "PCS", "FLT-02-A", 75000, 120000, 15, 50),
    ("PRT-FLT-005", "Air Filter Engine (17801-BZ050)", 6, "Denso OEM", "Toyota Avanza / Rush / Xenia 2012-2021", "PCS", "FLT-03-A", 65000, 105000, 12, 48),
    ("PRT-FLT-006", "Air Filter Engine (17801-0C010)", 6, "Toyota OEM", "Toyota Innova Reborn / Fortuner VRZ", "PCS", "FLT-03-B", 125000, 185000, 10, 36),
    ("PRT-FLT-007", "Air Filter Engine (17220-55A-Z01)", 6, "Honda OEM", "Honda Jazz GK5 / HR-V 1.5 / City GM6", "PCS", "FLT-03-C", 95000, 150000, 10, 32),
    ("PRT-FLT-008", "Cabin AC Filter Carbon Anti-Bacterial", 6, "Denso", "Toyota Avanza / Veloz / Rush / Xenia", "PCS", "FLT-04-A", 48000, 80000, 15, 62),
    ("PRT-FLT-009", "Cabin AC Filter Carbon Anti-Bacterial", 6, "Denso", "Toyota Innova Reborn / Fortuner VRZ", "PCS", "FLT-04-B", 55000, 90000, 12, 50),
    ("PRT-FLT-010", "Cabin AC Filter Carbon Anti-Bacterial", 6, "Denso", "Honda Brio / Mobilio / BR-V", "PCS", "FLT-04-C", 50000, 85000, 12, 45),

    # Fluids & Lubricants (Cat 7)
    ("PRT-LUB-001", "Synthetic Motor Oil 10W-40 4L", 7, "Shell Helix HX6", "Gasoline Engines Avanza, Xenia, Mobilio", "GAL", "LUB-01-A", 240000, 340000, 15, 65),
    ("PRT-LUB-002", "Fully Synthetic Motor Oil 5W-30 4L", 7, "Shell Helix HX8", "Modern Gasoline Engines VVT-i / i-VTEC", "GAL", "LUB-01-B", 360000, 495000, 15, 70),
    ("PRT-LUB-003", "Full Synthetic Eco 0W-20 4L", 7, "Pertamina Fastron Gold", "LCGC & Modern Japanese Engines", "GAL", "LUB-01-C", 320000, 450000, 12, 58),
    ("PRT-LUB-004", "Heavy Duty Diesel Engine Oil 15W-40 5L", 7, "Pertamina Meditran SX", "Diesel Engines Innova, Fortuner, Pajero", "GAL", "LUB-02-A", 260000, 365000, 15, 60),
    ("PRT-LUB-005", "Fully Synthetic Diesel Oil 5W-40 4L", 7, "Castrol Magnatec Diesel", "Common Rail CRDi Turbodiesel Engines", "GAL", "LUB-02-B", 390000, 540000, 10, 42),
    ("PRT-LUB-006", "Automatic Transmission Fluid ATF T-IV 4L", 7, "Toyota OEM", "Toyota 4-Speed Automatic Transmissions", "GAL", "LUB-03-A", 290000, 410000, 10, 38),
    ("PRT-LUB-007", "Continuously Variable Fluid CVT-FE 4L", 7, "Toyota OEM", "Toyota Sienta, Yaris CVT, Avanza CVT", "GAL", "LUB-03-B", 340000, 480000, 8, 30),
    ("PRT-LUB-008", "CVT Fluid HCF-2 3.5L", 7, "Honda OEM", "Honda Brio, Jazz, Mobilio, HR-V CVT", "GAL", "LUB-03-C", 330000, 470000, 8, 28),
    ("PRT-LUB-009", "Manual Transmission Gear Oil 75W-90 1L", 7, "Toyota OEM", "Manual Gearboxes & Transfer Cases", "BTL", "LUB-04-A", 70000, 105000, 15, 52),
    ("PRT-LUB-010", "Differential Gear Oil GL-5 80W-90 1L", 7, "Toyota OEM", "Rear Axle Differential Avanza, Innova", "BTL", "LUB-04-B", 65000, 95000, 15, 48),
    ("PRT-LUB-011", "Engine Radiator Coolant 50/50 4L", 7, "Prestone Premix", "Universal Cooling System All Cars", "GAL", "LUB-05-A", 85000, 130000, 15, 60),
    ("PRT-LUB-012", "Power Steering Fluid 1L", 7, "Prestone", "Hydraulic Power Steering Systems", "BTL", "LUB-05-B", 42000, 65000, 10, 35),

    # Exhaust & Emissions (Cat 8)
    ("PRT-EXH-001", "Exhaust Manifold Gasket 2NR", 8, "Toyota OEM", "Toyota Avanza 1.5 Dual VVT-i", "PCS", "EXH-01-A", 65000, 98000, 5, 14),
    ("PRT-EXH-002", "Exhaust Flange Donut Gasket", 8, "Federal", "Toyota Avanza / Rush / Xenia", "PCS", "EXH-01-B", 25000, 45000, 10, 32),
    ("PRT-EXH-003", "Upstream Oxygen Sensor (Air-Fuel)", 8, "Denso", "Toyota Avanza 2012-2020", "PCS", "EXH-02-A", 420000, 610000, 3, 9),
    ("PRT-EXH-004", "Exhaust Hanger Rubber Ring", 8, "RBI Thailand", "Universal Passenger Cars", "PCS", "EXH-02-B", 15000, 25000, 15, 45),
    ("PRT-EXH-005", "Muffler Tailpipe Tip Stainless", 8, "JS Racing Style", "Universal Exhaust Tip 2.5 Inch", "PCS", "EXH-03-A", 95000, 150000, 4, 12),

    # Cooling System (Cat 9)
    ("PRT-COL-001", "Aluminum Core Radiator Assembly", 9, "ADR Radiator", "Toyota Avanza 1.3/1.5 Manual 2012-2021", "UNIT", "COL-01-A", 620000, 890000, 3, 10),
    ("PRT-COL-002", "Aluminum Core Radiator Assembly AT", 9, "ADR Radiator", "Toyota Avanza 1.3/1.5 Matic 2012-2021", "UNIT", "COL-01-B", 680000, 970000, 3, 8),
    ("PRT-COL-003", "Radiator Upper Hose (Selang Atas)", 9, "Bando OEM", "Toyota Avanza / Rush", "PCS", "COL-02-A", 45000, 70000, 8, 24),
    ("PRT-COL-004", "Radiator Lower Hose (Selang Bawah)", 9, "Bando OEM", "Toyota Avanza / Rush", "PCS", "COL-02-B", 48000, 75000, 8, 22),
    ("PRT-COL-005", "Radiator Pressure Cap 1.1 Bar", 9, "Denso", "Universal Toyota, Honda, Suzuki, Daihatsu", "PCS", "COL-02-C", 38000, 60000, 12, 38),
    ("PRT-COL-006", "Radiator Electric Fan Motor", 9, "Denso", "Toyota Avanza / Rush / Veloz", "UNIT", "COL-03-A", 390000, 560000, 3, 11),

    # Wheels & Tires (Cat 10)
    ("PRT-WHL-001", "Radial Passenger Tire 185/65 R15", 10, "Bridgestone Ecopia", "Toyota Avanza, Veloz, Mobilio, Ertiga", "PCS", "WHL-01-A", 680000, 920000, 8, 32),
    ("PRT-WHL-002", "Radial Passenger Tire 205/65 R16", 10, "Dunlop Enasave", "Toyota Innova Reborn 2.0 / 2.4", "PCS", "WHL-01-B", 850000, 1150000, 6, 24),
    ("PRT-WHL-003", "Radial All-Terrain Tire 265/65 R17", 10, "Bridgestone Dueler", "Toyota Fortuner, Mitsubishi Pajero Sport", "PCS", "WHL-01-C", 1650000, 2200000, 4, 16),
    ("PRT-WHL-004", "Radial City Tire 175/65 R14", 10, "GT Radial Champiro", "Honda Brio, Toyota Calya, Daihatsu Sigra", "PCS", "WHL-02-A", 450000, 620000, 8, 30),
    ("PRT-WHL-005", "Front Wheel Hub Bearing Assembly", 10, "Koyo Japan", "Toyota Avanza / Rush / Xenia", "PCS", "WHL-03-A", 185000, 270000, 6, 20),
    ("PRT-WHL-006", "Rear Wheel Bearing Kit", 10, "NSK Japan", "Toyota Avanza / Xenia with Seal & Collar", "KIT", "WHL-03-B", 145000, 215000, 6, 22),
    ("PRT-WHL-007", "Tubeless Rubber Tire Valve TR414", 10, "Pacific", "Universal All Tubeless Wheels", "PCS", "WHL-04-A", 4000, 10000, 50, 200),
    ("PRT-WHL-008", "Wheel Balance Lead Weight 15g", 10, "Teco", "Universal Wheel Balancing", "PCS", "WHL-04-B", 2000, 5000, 100, 450),

    # Air Conditioning (AC) (Cat 11)
    ("PRT-AC-001", "AC Compressor 10S11C Complete", 11, "Denso Original", "Toyota Avanza 1.3 / 1.5 2012-2021", "UNIT", "AC-01-A", 1850000, 2550000, 2, 7),
    ("PRT-AC-002", "AC Compressor 10S15C Complete", 11, "Denso Original", "Toyota Innova Bensin 2.0 / Diesel 2.5", "UNIT", "AC-01-B", 2200000, 3050000, 2, 6),
    ("PRT-AC-003", "AC Condenser Assembly Multi-Flow", 11, "Denso", "Toyota Avanza / Rush", "UNIT", "AC-02-A", 520000, 750000, 3, 9),
    ("PRT-AC-004", "AC Evaporator Cooling Coil", 11, "Denso", "Toyota Avanza / Xenia Depan", "UNIT", "AC-02-B", 420000, 610000, 3, 8),
    ("PRT-AC-005", "AC Expansion Valve Block Type", 11, "Fujikoki", "Toyota Avanza / Innova / Rush", "PCS", "AC-03-A", 110000, 170000, 6, 20),
    ("PRT-AC-006", "AC Magnetic Clutch Assembly", 11, "Denso", "Toyota Avanza 1.3 / 1.5", "SET", "AC-03-B", 310000, 450000, 4, 12),
    ("PRT-AC-007", "Freon R134a Pure Refrigerant 13.6kg", 11, "Klea Ineos", "Workshop AC Recharging Station", "TNK", "AC-04-A", 1150000, 1600000, 2, 6),
    ("PRT-AC-008", "PAG 46 AC Compressor Oil 250ml", 11, "Denso Oil 8", "R134a AC Compressor Lubrication", "BTL", "AC-04-B", 65000, 105000, 10, 30),

    # Body, Glass & Accessories (Cat 12)
    ("PRT-BDY-001", "Wiper Blade Frameless Pair 20\"+16\"", 12, "Bosch Clear Advantage", "Toyota Avanza, Veloz, Rush, Xenia", "PAIR", "BDY-01-A", 85000, 135000, 12, 45),
    ("PRT-BDY-002", "Wiper Blade Frameless Pair 22\"+16\"", 12, "Bosch Clear Advantage", "Honda Brio, Mobilio, BR-V", "PAIR", "BDY-01-B", 90000, 140000, 10, 40),
    ("PRT-BDY-003", "Wiper Blade Frameless Pair 26\"+16\"", 12, "Bosch Clear Advantage", "Toyota Innova Reborn / Fortuner", "PAIR", "BDY-01-C", 110000, 170000, 10, 38),
    ("PRT-BDY-004", "Rear Wiper Blade 12 Inch", 12, "Bosch Rear", "Avanza, Innova, Brio, Jazz", "PCS", "BDY-01-D", 45000, 75000, 10, 35),
    ("PRT-BDY-005", "Automotive Door Trim Clip Rivet (Bag 50pcs)", 12, "Federal OEM", "Universal Toyota & Daihatsu", "BAG", "BDY-02-A", 25000, 50000, 15, 50),
    ("PRT-BDY-006", "Under-Engine Cover Guard Plastic", 12, "Federal Aftermarket", "Toyota Avanza 2012-2021", "PCS", "BDY-03-A", 95000, 160000, 4, 11),
    ("PRT-BDY-007", "Washer Fluid Concentrate 1000ml", 12, "Prestone Bug Wash", "Universal Windshield Washer", "BTL", "BDY-04-A", 25000, 45000, 20, 75),
]

# 5. Customers (45 Customers)
customers_data = [
    ("CUST-001", "PT Artha Prima Logistik", "021-55667788", "fleet@arthaprima.co.id", "Kawasan Pergudangan Pluit Blok C No. 5", "CORPORATE"),
    ("CUST-002", "CV Sumber Rejeki Express", "021-88990011", "operasional@sumberrejeki.com", "Jl. Daan Mogot Km. 14 No. 22", "CORPORATE"),
    ("CUST-003", "PT Transindo Mega Sejahtera", "021-77881122", "procurement@transindo.co.id", "Kawasan Industri Pulo Gadung Kav. 18", "CORPORATE"),
    ("CUST-004", "PT Berkat Jaya Cargo", "021-33445566", "maintenance@berkatjaya.co.id", "Jl. TB Simatupang No. 78", "CORPORATE"),
    ("CUST-005", "Hendra Gunawan", "081211223344", "hendragunawan@gmail.com", "Jl. Kebon Jeruk Baru No. 12, Jakarta Barat", "INDIVIDUAL"),
    ("CUST-006", "Dewi Lestari", "081322334455", "dewi.lestari@yahoo.com", "Jl. Tebet Barat Dalam VII No. 4, Jakarta Selatan", "INDIVIDUAL"),
    ("CUST-007", "Andi Wijaya", "081233445566", "andi.wijaya88@gmail.com", "Perumahan BSD City Sektor 1.2 Blok B/14, Tangerang", "INDIVIDUAL"),
    ("CUST-008", "Bambang Supriyanto", "081144556677", "bambang.supriyanto@gmail.com", "Jl. Cempaka Putih Tengah No. 33, Jakarta Pusat", "INDIVIDUAL"),
    ("CUST-009", "Rina Suryani", "081355667788", "rina.suryani@gmail.com", "Jl. Margonda Raya No. 110, Depok", "INDIVIDUAL"),
    ("CUST-010", "Ahmad Fauzi", "081266778899", "ahmad.fauzi@yahoo.com", "Kompleks Harapan Indah Blok DB No. 9, Bekasi", "INDIVIDUAL"),
    ("CUST-011", "Siti Aminah", "081377889900", "siti.aminah@gmail.com", "Jl. Kelapa Nias Raya Blok QA/2, Kelapa Gading", "INDIVIDUAL"),
    ("CUST-012", "Dicky Chandra", "081188990011", "dicky.chandra@gmail.com", "Jl. Rawamangun Muka Timur No. 15, Jakarta Timur", "INDIVIDUAL"),
    ("CUST-013", "Maya Anggraini", "081299001122", "maya.anggraini@gmail.com", "Jl. Cinere Raya No. 45, Depok", "INDIVIDUAL"),
    ("CUST-014", "Ferry Salim", "081300112233", "ferry.salim@yahoo.com", "Apartemen Green Bay Tower D-12A, Pluit", "INDIVIDUAL"),
    ("CUST-015", "Wahyudi", "081111223344", "wahyudi.transport@gmail.com", "Jl. Raya Serpong No. 89, Tangerang Selatan", "INDIVIDUAL"),
    ("CUST-016", "Ratna Sari", "081222334455", "ratnasari.id@gmail.com", "Jl. Sunter Podomoro Blok E No. 8, Jakarta Utara", "INDIVIDUAL"),
    ("CUST-017", "Budi Hermawan", "081333445566", "budi.hermawan77@gmail.com", "Jl. Senopati Dalam No. 19, Kebayoran Baru", "INDIVIDUAL"),
    ("CUST-018", "Indra Gunawan", "081144556688", "indra.gunawan@yahoo.com", "Jl. Bintaro Utama Sektor 5 No. 27, Tangerang", "INDIVIDUAL"),
    ("CUST-019", "PT Samudra Logistik Internasional", "021-44556677", "admin@samudralogistik.co.id", "Tanjung Priok Port Area Blok F No. 1", "CORPORATE"),
    ("CUST-020", "Eko Yulianto", "081255667799", "eko.yulianto@gmail.com", "Jl. Utan Kayu Raya No. 56, Jakarta Timur", "INDIVIDUAL"),
    ("CUST-021", "Yudi Setiawan", "081366778800", "yudi.setiawan@gmail.com", "Jl. Bintara Jaya No. 102, Bekasi", "INDIVIDUAL"),
    ("CUST-022", "Kurniawan Dwi", "081177889911", "kurniawan.dwi@yahoo.com", "Jl. Danau Sunter Utara Blok F No. 22", "INDIVIDUAL"),
    ("CUST-023", "PT Berdikari Mitra Transport", "021-88776655", "fleet@berdikaritransport.com", "Jl. Mayor Oking No. 88, Cibinong, Bogor", "CORPORATE"),
    ("CUST-024", "Nurul Hidayah", "081288990022", "nurul.hidayah@gmail.com", "Jl. Kemang Raya No. 34, Jakarta Selatan", "INDIVIDUAL"),
    ("CUST-025", "Taufiq Rahman", "081399001133", "taufiq.rahman@gmail.com", "Jl. Radio Dalam Raya No. 15, Gandaria", "INDIVIDUAL"),
    ("CUST-026", "Agus Budiman", "081100112244", "agus.budiman@yahoo.com", "Jl. Ciledug Raya No. 67, Tangerang", "INDIVIDUAL"),
    ("CUST-027", "Sri Wahyuni", "081211224455", "sri.wahyuni@gmail.com", "Jl. Gajah Mada No. 182, Jakarta Barat", "INDIVIDUAL"),
    ("CUST-028", "Doni Prasetyo", "081322335566", "doni.prasetyo@gmail.com", "Jl. Pahlawan Revolusi No. 44, Pondok Bambu", "INDIVIDUAL"),
    ("CUST-029", "Linda Marlina", "081133446677", "linda.marlina@yahoo.com", "Perumahan Taman Galaxy Blok A No. 18, Bekasi", "INDIVIDUAL"),
    ("CUST-030", "Rudi Hartono", "081244557788", "rudi.hartono99@gmail.com", "Jl. Fatmawati Raya No. 50, Jakarta Selatan", "INDIVIDUAL"),
    ("CUST-031", "PT Surya Graha Mandiri", "021-55443322", "general@suryagraha.co.id", "Komp. Sudirman Central Business District (SCBD) Lot 9", "CORPORATE"),
    ("CUST-032", "Lukman Hakim", "081355668899", "lukman.hakim@gmail.com", "Jl. Palmerah Barat No. 28, Jakarta Barat", "INDIVIDUAL"),
    ("CUST-033", "Mega Utami", "081166779900", "mega.utami@yahoo.com", "Jl. Ampera Raya No. 16, Cilandak", "INDIVIDUAL"),
    ("CUST-034", "Ade Kurniawan", "081277880011", "ade.kurniawan@gmail.com", "Jl. Jatibening Estate Blok E No. 5, Bekasi", "INDIVIDUAL"),
    ("CUST-035", "Fitri Handayani", "081388991122", "fitri.handayani@gmail.com", "Jl. Tanjung Duren Barat No. 39, Jakarta Barat", "INDIVIDUAL"),
    ("CUST-036", "Anton Wijaya", "081199002233", "anton.wijaya@yahoo.com", "Jl. Muara Karang Blok P2 No. 12, Pluit", "INDIVIDUAL"),
    ("CUST-037", "PT Cemerlang Abadi Rent", "021-66778899", "operations@cemerlangrent.co.id", "Jl. Boulevard Barat Raya LC 7 No. 1, Kelapa Gading", "CORPORATE"),
    ("CUST-038", "Suryo Pranoto", "081200113344", "suryo.pranoto@gmail.com", "Jl. Cikini Raya No. 72, Menteng, Jakarta Pusat", "INDIVIDUAL"),
    ("CUST-039", "Ika Permata", "081311224455", "ika.permata@yahoo.com", "Jl. Pejaten Barat No. 89, Jakarta Selatan", "INDIVIDUAL"),
    ("CUST-040", "Bayu Wicaksono", "081122335566", "bayu.wicaksono@gmail.com", "Jl. Puri Indah Raya Blok H No. 3, Kembangan", "INDIVIDUAL"),
    ("CUST-041", "Yanti Susanti", "081233446677", "yanti.susanti@gmail.com", "Jl. Kali Malang Raya No. 42, Duren Sawit", "INDIVIDUAL"),
    ("CUST-042", "Hadi Sutanto", "081344557788", "hadi.sutanto@yahoo.com", "Jl. Alternatif Cibubur Km. 4 No. 15", "INDIVIDUAL"),
    ("CUST-043", "PT Dinamika Sukses Berjaya", "021-99887766", "info@dinamikaberkaya.co.id", "Kawasan Industri MM2100 Blok DD No. 5, Cikarang", "CORPORATE"),
    ("CUST-044", "Denny Santoso", "081155668899", "denny.santoso@gmail.com", "Jl. Tomang Raya No. 18, Jakarta Barat", "INDIVIDUAL"),
    ("CUST-045", "Widodo", "081266779900", "widodo.auto@gmail.com", "Jl. Bendungan Hilir No. 64, Tanah Abang", "INDIVIDUAL")
]

# 6. Vehicles (65 Vehicles)
vehicles_raw = [
    (1, "B 1024 BAA", "MHF11BA40K123401", "Toyota", "Avanza 1.3 G M/T", 2019, "Silver Metalik", "MANUAL", 54200),
    (1, "B 1025 BAB", "MHF11BA40K123402", "Toyota", "Avanza 1.3 G M/T", 2019, "Putih", "MANUAL", 61500),
    (1, "B 1026 BAC", "MHF11BA40K123403", "Toyota", "Avanza 1.3 G M/T", 2020, "Hitam", "MANUAL", 48000),
    (2, "B 2045 CAD", "MHF21CA50L234501", "Daihatsu", "Gran Max Pick Up 1.5", 2018, "Putih", "MANUAL", 98400),
    (2, "B 2046 CAE", "MHF21CA50L234502", "Daihatsu", "Gran Max Minibus 1.3", 2019, "Hitam", "MANUAL", 82300),
    (3, "B 9188 TX", "MHK12DA60M345601", "Toyota", "Innova Reborn 2.4 G Diesel", 2020, "Abu-Abu Metalik", "MANUAL", 58100),
    (3, "B 9189 TX", "MHK12DA60M345602", "Toyota", "Innova Reborn 2.4 V Diesel", 2021, "Hitam Metalik", "AUTOMATIC", 41200),
    (4, "B 3311 BCD", "MHK31EA70N456701", "Mitsubishi", "Colt Diesel Canter FE 71", 2019, "Kuning", "MANUAL", 112000),
    (5, "B 1892 KLA", "MHF11BA40J111222", "Toyota", "Avanza 1.3 Veloz A/T", 2018, "Putih Mutiara", "AUTOMATIC", 65200),
    (6, "B 2341 TQZ", "MHES41A30H222333", "Honda", "Jazz RS 1.5 CVT", 2017, "Kuning Mutiara", "CVT", 71400),
    (7, "B 1455 AC", "MHK12DA60L333444", "Toyota", "Innova 2.0 V Bensin A/T", 2019, "Hitam Mica", "AUTOMATIC", 49800),
    (8, "B 9081 SAA", "MHK21FA50K444555", "Mitsubishi", "Xpander Ultimate 1.5 A/T", 2019, "Silver Quartz", "AUTOMATIC", 53400),
    (9, "B 1243 CD", "MHES61A40M555666", "Honda", "HR-V 1.5 E Special Edition CVT", 2020, "Putih Orchid", "CVT", 38900),
    (10, "B 7766 JKL", "MHF12BA50N666777", "Toyota", "Rush 1.5 S TRD Sportivo A/T", 2021, "Maroon Metalik", "AUTOMATIC", 32100),
    (11, "B 2981 WQ", "MHF31GA60K777888", "Suzuki", "All New Ertiga GL M/T", 2018, "Abu-Abu Magma", "MANUAL", 68500),
    (12, "B 8820 PQR", "MHF13BA60L888999", "Toyota", "Calya 1.2 G A/T", 2019, "Orange Metalik", "AUTOMATIC", 47200),
    (13, "B 1675 UV", "MHES21A20J999000", "Honda", "Brio Satya E M/T", 2018, "Merah Rallye", "MANUAL", 59300),
    (14, "B 6543 DDA", "MHK14DA70N123789", "Toyota", "Fortuner 2.4 VRZ Diesel A/T", 2021, "Super White", "AUTOMATIC", 36400),
    (15, "B 3421 RTY", "MHF11BA40M234890", "Toyota", "Avanza 1.5 Veloz M/T", 2020, "Hitam", "MANUAL", 42100),
    (16, "B 5432 ZZZ", "MHES31A30K345901", "Honda", "Mobilio E CVT", 2019, "Putih Taffeta", "CVT", 51600),
    (17, "B 1109 KLN", "MHF11BA40P456012", "Toyota", "All New Avanza 1.5 G CVT", 2022, "Purplish Silver", "CVT", 18200),
    (18, "B 7654 FGH", "MHK22FA60M567123", "Mitsubishi", "Pajero Sport Dakar 2.4 4x2 A/T", 2020, "Deep Bronze Metalik", "AUTOMATIC", 44300),
    (19, "B 9321 XCV", "MHK31EA70K678234", "Mitsubishi", "Fuso Canter FE 74 HD", 2018, "Kuning", "MANUAL", 145000),
    (19, "B 9322 XCV", "MHK31EA70K678235", "Mitsubishi", "Fuso Canter FE 74 HD", 2018, "Kuning", "MANUAL", 138500),
    (20, "B 2841 MN", "MHF22CA60L789345", "Daihatsu", "All New Terios R Deluxe A/T", 2019, "Bronze Metalik", "AUTOMATIC", 56200),
    (21, "B 1982 UIO", "MHF11BA40H890456", "Toyota", "Avanza 1.3 E M/T", 2017, "Silver", "MANUAL", 89100),
    (22, "B 4321 JHG", "MHES51A40N901567", "Honda", "All New City Sedan CVT", 2021, "Platinum White Pearl", "CVT", 29400),
    (23, "B 8123 TRP", "MHF11BA40K012678", "Toyota", "Avanza 1.3 G M/T", 2019, "Silver Metalik", "MANUAL", 76400),
    (23, "B 8124 TRP", "MHF11BA40K012679", "Toyota", "Avanza 1.3 G M/T", 2019, "Putih", "MANUAL", 80100),
    (24, "B 6789 PLM", "MHES21A20L123789", "Honda", "Brio RS Urbanite Edition CVT", 2021, "Phoenix Orange Pearl", "CVT", 25300),
    (25, "B 3141 PI", "MHK12DA60P234890", "Toyota", "Innova Reborn 2.4 V Diesel A/T", 2022, "Phantom Brown Metalik", "AUTOMATIC", 16800),
    (26, "B 5544 KMN", "MHF31GA60N345901", "Suzuki", "All New Ertiga Sport A/T", 2021, "Cool Black Metalik", "AUTOMATIC", 31200),
    (27, "B 1290 OPA", "MHF13BA60M456012", "Toyota", "Calya 1.2 G M/T", 2020, "Merah", "MANUAL", 43500),
    (28, "B 4433 EWQ", "MHK21FA50L567123", "Mitsubishi", "Xpander Cross 1.5 Premium A/T", 2020, "Sunrise Orange Metalik", "AUTOMATIC", 41800),
    (29, "B 7890 VBN", "MHES61A40P678234", "Honda", "All New HR-V 1.5 SE CVT", 2022, "Sand Khaki Pearl", "CVT", 14200),
    (30, "B 2211 LKJ", "MHK14DA70M789345", "Toyota", "Fortuner 2.8 GR Sport 4x2 A/T", 2022, "Attitude Black", "AUTOMATIC", 19500),
    (31, "B 1111 SCB", "MHK12DA60N890456", "Toyota", "Innova Venturer 2.4 Diesel A/T", 2021, "Red Mica Metalik", "AUTOMATIC", 38700),
    (31, "B 1112 SCB", "MHK12DA60N890457", "Toyota", "Innova Venturer 2.4 Diesel A/T", 2021, "Hitam", "AUTOMATIC", 42100),
    (32, "B 6677 TYU", "MHF11BA40L901567", "Toyota", "Avanza 1.3 G A/T", 2020, "Putih", "AUTOMATIC", 45600),
    (33, "B 9988 DSA", "MHES41A30J012678", "Honda", "Jazz RS 1.5 M/T", 2018, "Modern Steel Metalik", "MANUAL", 63400),
    (34, "B 3322 POI", "MHF21CA50K123789", "Daihatsu", "Xenia 1.3 R Deluxe M/T", 2019, "Silver Metalik", "MANUAL", 59800),
    (35, "B 5566 TRE", "MHES21A20K234890", "Honda", "Brio Satya E CVT", 2019, "Carnival Yellow", "CVT", 52100),
    (36, "B 7788 QWE", "MHK21FA50P345901", "Mitsubishi", "New Xpander Sport CVT", 2022, "Blade Silver Metalik", "CVT", 17900),
    (37, "B 9001 CMR", "MHF11BA40N456012", "Toyota", "Avanza 1.3 G M/T", 2021, "Silver Metalik", "MANUAL", 46200),
    (37, "B 9002 CMR", "MHF11BA40N456013", "Toyota", "Avanza 1.3 G M/T", 2021, "Putih", "MANUAL", 49100),
    (37, "B 9003 CMR", "MHK12DA60N456014", "Toyota", "Innova Reborn 2.0 G A/T", 2021, "Hitam Metalik", "AUTOMATIC", 39800),
    (38, "B 4411 MNK", "MHF12BA50M567123", "Toyota", "Rush 1.5 S TRD A/T", 2020, "Silver Mica Metalik", "AUTOMATIC", 43200),
    (39, "B 8833 VFD", "MHES31A30H678234", "Honda", "Mobilio RS CVT", 2017, "Crystal Black Pearl", "CVT", 78900),
    (40, "B 1234 BY", "MHK12DA60P789345", "Toyota", "All New Kijang Innova Zenix 2.0 V", 2022, "Platinum White Pearl", "CVT", 11200),
    (41, "B 5678 YT", "MHF13BA60N890456", "Toyota", "Calya 1.2 E M/T", 2021, "Abu-Abu Metalik", "MANUAL", 36700),
    (42, "B 9102 HU", "MHK22FA60L901567", "Mitsubishi", "Pajero Sport Exceed 4x2 A/T", 2019, "Sterling Silver", "AUTOMATIC", 62400),
    (43, "B 8899 DSB", "MHF11BA40M012678", "Toyota", "Avanza 1.3 G M/T", 2020, "Putih", "MANUAL", 64500),
    (43, "B 8890 DSB", "MHF11BA40M012679", "Toyota", "Avanza 1.3 G M/T", 2020, "Hitam", "MANUAL", 67800),
    (44, "B 2345 DN", "MHES41A30L123789", "Honda", "Jazz RS 1.5 CVT", 2020, "Rallye Red", "CVT", 41300),
    (45, "B 6781 WD", "MHF31GA60M234890", "Suzuki", "All New Ertiga GX M/T", 2020, "Metallic Magma Gray", "MANUAL", 48900)
]

# Service types catalog
service_catalog = [
    ("Periodic Maintenance 10,000 KM", ["PRT-LUB-001", "PRT-FLT-001"], [("Periodic 10k Maintenance Inspection", 1.5, 250000)]),
    ("Periodic Maintenance 20,000 KM", ["PRT-LUB-002", "PRT-FLT-001", "PRT-FLT-008", "PRT-ELC-005"], [("Periodic 20k Maintenance Service & Brake Clean", 2.0, 350000)]),
    ("Periodic Maintenance 40,000 KM Major", ["PRT-LUB-002", "PRT-FLT-001", "PRT-FLT-005", "PRT-FLT-008", "PRT-BRK-010", "PRT-LUB-011"], [("Major 40k Service & Comprehensive Inspection", 3.5, 600000)]),
    ("Front & Rear Brake Overhaul", ["PRT-BRK-002", "PRT-BRK-004", "PRT-BRK-010"], [("Front & Rear Brake Replacement & Caliper Overhaul", 2.0, 350000), ("Brake Rotor Skimming & Bleeding", 1.0, 200000)]),
    ("Diesel Major Service & Fuel Filter", ["PRT-LUB-004", "PRT-FLT-002", "PRT-FLT-004", "PRT-FLT-009"], [("Diesel Periodic Service & Fuel System Purge", 2.5, 450000)]),
    ("Automatic Transmission Flushing & Service", ["PRT-TRN-008", "PRT-LUB-006"], [("ATF Machine Flushing & Strainer Replacement", 2.0, 400000)]),
    ("Manual Clutch System Overhaul", ["PRT-TRN-001", "PRT-TRN-002", "PRT-TRN-003", "PRT-LUB-009"], [("Transmission Drop & Clutch System Replacement", 4.0, 750000), ("Flywheel Resurfacing", 1.0, 250000)]),
    ("Front Suspension & Steering Overhaul", ["PRT-SUS-001", "PRT-SUS-002", "PRT-SUS-006", "PRT-SUS-007"], [("Front Strut & Tie Rod Replacement", 3.0, 500000), ("4-Wheel Alignment (Spooring) & Balancing", 1.0, 250000)]),
    ("Complete AC Service & Evaporator Clean", ["PRT-AC-008", "PRT-FLT-008"], [("AC System Flushing, Evaporator Wash & Freon Refill", 2.5, 450000)]),
    ("Battery Replacement & Electrical Check", ["PRT-ELC-001"], [("Battery Installation & Charging System Diagnostic", 0.5, 100000)]),
    ("Cooling System Overhaul & Radiator Service", ["PRT-COL-001", "PRT-COL-003", "PRT-COL-004", "PRT-LUB-011"], [("Radiator Assembly Replacement & System Bleeding", 2.0, 350000)]),
    ("Engine Tune-Up & Throttle Body Clean", ["PRT-ELC-004", "PRT-FLT-005"], [("Engine Tune-up, Spark Plugs & Intake Chamber Clean", 1.5, 300000)]),
    ("Tire Replacement & 4-Wheel Balancing", ["PRT-WHL-001", "PRT-WHL-007", "PRT-WHL-008"], [("Tire Mounting, Dynamic Balancing & Nitrogen Fill", 1.5, 250000)])
]

complaints = [
    ("Suara mendecit tajam saat menginjak pedal rem di kecepatan rendah", "Kampas rem depan menipis (<2mm), piringan disc ada alur tipis."),
    ("Mesin terasa brebet saat akselerasi awal dan konsumsi BBM meningkat", "Busi kotor berkarbon, throttle body terdapat kerak oli pekat."),
    ("AC terasa kurang dingin saat siang hari dan hembusan angin kecil", "Filter kabin mampet debu pekat, tekanan freon rendah butuh vacuum & oli baru."),
    ("Suspensi depan bunyi gluduk-gluduk saat melintasi jalan bergelombang", "Shock absorber bocor oli, tie rod end dan link stabilizer aus oblak."),
    ("Pedal kopling terasa sangat keras dan tarikan mesin berat/selip di tanjakan", "Kampas kopling tipis habis, dekrup matahari mengeras dan release bearing berisik."),
    ("Starter mesin terasa lambat dan indikator baterai berkedip di speedometer", "Voltase aki drop ke 10.2V saat cranking, daya simpan CCA baterai menurun."),
    ("Jadwal servis berkala rutin sesuai kilometer berkendara", "Pemeriksaan 24 titik kendaraan normal, penggantian oli dan filter preventif."),
    ("Perpindahan tuas transmisi otomatis terasa menghentak (jedug) saat masuk gigi D atau R", "Oli matic ATF telah terdegradasi warna hitam, filter transmisi tersumbat residu kampas kopling matic."),
    ("Temperatur mesin cepat naik / overheat saat macet di siang terik", "Kondensor dan kisi radiator berlumut kotor, thermostat terlambat membuka, cairan coolant keruh."),
    ("Roda terasa bergetar keras pada setir saat kecepatan di atas 80 km/jam", "Ban unbalance, velg depan terdapat peyang mikro, tekanan ban tidak seimbang.")
]

print("Building SQL script...")

out_sql = []
out_sql.append("-- ========================================================")
out_sql.append("-- CAR WORKSHOP INVENTORY - 2022 MASSIVE SEED DATA")
out_sql.append("-- Generated with authentic Indonesian automotive datasets")
out_sql.append("-- ========================================================\n")
out_sql.append("USE car_workshop_db;\n")
out_sql.append("SET FOREIGN_KEY_CHECKS = 0;\n")

# Clear tables
for t in ["service_labor_items", "service_parts_used", "service_transactions", "stock_adjustments", "stock_ledger", "purchase_order_items", "purchase_orders", "vehicles", "customers", "parts", "categories", "suppliers", "users"]:
    out_sql.append(f"TRUNCATE TABLE {t};")
out_sql.append("SET FOREIGN_KEY_CHECKS = 1;\n")

# Insert Users
out_sql.append("-- 1. USERS")
for u in users_data:
    p_hash = sha256(u[1])
    out_sql.append(f"INSERT INTO users (username, password_hash, full_name, email, phone, role) VALUES ({escape_sql(u[0])}, '{p_hash}', {escape_sql(u[2])}, {escape_sql(u[3])}, {escape_sql(u[4])}, '{u[5]}');")
out_sql.append("")

# Insert Categories
out_sql.append("-- 2. CATEGORIES")
for c in categories_data:
    out_sql.append(f"INSERT INTO categories (name, description) VALUES ({escape_sql(c[0])}, {escape_sql(c[1])});")
out_sql.append("")

# Insert Suppliers
out_sql.append("-- 3. SUPPLIERS")
for s in suppliers_data:
    out_sql.append(f"INSERT INTO suppliers (supplier_code, name, contact_person, phone, email, address, city, payment_terms) VALUES ({escape_sql(s[0])}, {escape_sql(s[1])}, {escape_sql(s[2])}, {escape_sql(s[3])}, {escape_sql(s[4])}, {escape_sql(s[5])}, {escape_sql(s[6])}, {escape_sql(s[7])});")
out_sql.append("")

# Insert Parts
out_sql.append("-- 4. PARTS")
parts_by_code = {}
part_id_counter = 1
for p in parts_raw:
    p_code, p_name, p_cat, p_brand, p_comp, p_uom, p_bin, p_cost, p_sell, p_min, p_stock = p
    parts_by_code[p_code] = {
        "id": part_id_counter,
        "name": p_name,
        "cost": p_cost,
        "sell": p_sell,
        "stock": p_stock,
        "min": p_min
    }
    part_id_counter += 1
    out_sql.append(f"INSERT INTO parts (part_number, part_name, category_id, brand, compatibility, unit_of_measure, storage_bin, cost_price, selling_price, min_stock, current_stock, status) VALUES ({escape_sql(p_code)}, {escape_sql(p_name)}, {p_cat}, {escape_sql(p_brand)}, {escape_sql(p_comp)}, {escape_sql(p_uom)}, {escape_sql(p_bin)}, {p_cost}, {p_sell}, {p_min}, {p_stock}, 'IN_STOCK');")
out_sql.append("")

# Insert Customers
out_sql.append("-- 5. CUSTOMERS")
for c in customers_data:
    out_sql.append(f"INSERT INTO customers (customer_code, name, phone, email, address, customer_type) VALUES ({escape_sql(c[0])}, {escape_sql(c[1])}, {escape_sql(c[2])}, {escape_sql(c[3])}, {escape_sql(c[4])}, '{c[5]}');")
out_sql.append("")

# Insert Vehicles
out_sql.append("-- 6. VEHICLES")
for v in vehicles_raw:
    out_sql.append(f"INSERT INTO vehicles (customer_id, plate_number, vin, make, model, year, color, transmission, odometer) VALUES ({v[0]}, {escape_sql(v[1])}, {escape_sql(v[2])}, {escape_sql(v[3])}, {escape_sql(v[4])}, {v[5]}, {escape_sql(v[6])}, '{v[7]}', {v[8]});")
out_sql.append("")

# Now Generate 2022 Chronological Events:
# We will simulate 2022 from Jan 1, 2022 to Dec 31, 2022.
# 1. Purchase Orders (Procurement)
# 2. Service Transactions (Consumption)
# 3. Stock Adjustments (Audits)
# 4. Stock Ledger Entries (Relational Audit Trail)

# We track dynamic stock balances for stock_ledger
part_stock_tracker = {}
for code, info in parts_by_code.items():
    part_stock_tracker[info["id"]] = 0 # will accumulate from POs and deductions

start_date = datetime(2022, 1, 3, 8, 30)

events = [] # list of (datetime, type, data)

# Generate Initial Bulk Stock Purchase Orders in early Jan 2022
# 12 Initial POs across suppliers to stock the warehouse
for sup_id in range(1, len(suppliers_data) + 1):
    po_date = start_date + timedelta(days=random.randint(0, 4), hours=random.randint(1, 6))
    events.append((po_date, "PO_INITIAL", sup_id))

# Generate Regular Weekly Procurement Orders throughout 2022 (approx 100 POs)
cur_day = start_date + timedelta(days=7)
end_date = datetime(2022, 12, 30, 17, 0)

po_counter = 1
while cur_day < end_date:
    # 2 POs per week on average
    for _ in range(random.choice([2, 3])):
        s_id = random.randint(1, len(suppliers_data))
        p_time = cur_day + timedelta(days=random.randint(0, 5), hours=random.randint(8, 16))
        events.append((p_time, "PO_REGULAR", s_id))
    cur_day += timedelta(days=7)

# Generate Customer Repair Transactions (approx 300 throughout 2022)
cur_day = start_date + timedelta(days=5)
while cur_day < end_date:
    daily_trxs = random.choice([0, 1, 2, 2, 3, 3, 4])
    for _ in range(daily_trxs):
        v_idx = random.randint(1, len(vehicles_raw))
        t_hour = random.randint(8, 16)
        t_min = random.randint(0, 59)
        t_time = cur_day.replace(hour=t_hour, minute=t_min)
        events.append((t_time, "SERVICE_TRX", v_idx))
    cur_day += timedelta(days=1)

# Generate Periodic Stock Adjustments (approx 25 throughout 2022)
for m in range(1, 13):
    for adj_idx in range(2):
        adj_time = datetime(2022, m, random.randint(15, 28), random.randint(16, 18), 30)
        events.append((adj_time, "STOCK_ADJUSTMENT", None))

# Sort all events chronologically
events.sort(key=lambda x: x[0])

po_id_seq = 1
trx_id_seq = 1
adj_id_seq = 1

po_sql_list = []
po_items_sql_list = []
trx_sql_list = []
trx_parts_sql_list = []
trx_labor_sql_list = []
adj_sql_list = []
ledger_sql_list = []

part_ids_list = list(parts_by_code.keys())

for dt, ev_type, ev_data in events:
    date_str = dt.strftime("%Y-%m-%d")
    datetime_str = dt.strftime("%Y-%m-%d %H:%M:%S")

    if ev_type in ("PO_INITIAL", "PO_REGULAR"):
        sup_id = ev_data
        po_num = f"PO-2022-{po_id_seq:04d}"
        exp_date = (dt + timedelta(days=random.randint(2, 5))).strftime("%Y-%m-%d")
        rec_date = (dt + timedelta(days=random.randint(2, 5))).strftime("%Y-%m-%d")

        is_initial = (ev_type == "PO_INITIAL")
        num_items = random.randint(8, 14) if is_initial else random.randint(3, 7)
        sampled_parts = random.sample(part_ids_list, min(num_items, len(part_ids_list)))

        total_po_amount = 0
        items_temp = []

        for p_code in sampled_parts:
            p_info = parts_by_code[p_code]
            p_id = p_info["id"]
            cost = p_info["cost"]
            qty = random.randint(15, 30) if is_initial else random.randint(4, 15)
            subtotal = cost * qty
            total_po_amount += subtotal

            items_temp.append((po_id_seq, p_id, qty, qty, cost, subtotal))

            # Update stock tracker and write to stock ledger
            stock_before = part_stock_tracker[p_id]
            stock_after = stock_before + qty
            part_stock_tracker[p_id] = stock_after

            rec_datetime = f"{rec_date} 10:15:00"
            ledger_sql_list.append(
                f"INSERT INTO stock_ledger (transaction_time, part_id, transaction_type, reference_no, quantity_change, stock_before, stock_after, unit_cost, total_cost, notes, created_by) "
                f"VALUES ('{rec_datetime}', {p_id}, 'PROCUREMENT_IN', '{po_num}', {qty}, {stock_before}, {stock_after}, {cost}, {subtotal}, 'Penerimaan barang dari PO {po_num}', {random.choice([3, 4])});"
            )

        po_status = "RECEIVED_FULL"
        pay_status = "PAID" if dt.month < 12 else random.choice(["PAID", "PAID", "PARTIALLY_PAID"])
        creator = random.choice([3, 4]) # clerk budi or siti

        po_sql_list.append(
            f"INSERT INTO purchase_orders (id, po_number, supplier_id, order_date, expected_date, received_date, status, payment_status, total_amount, notes, created_by, created_at) "
            f"VALUES ({po_id_seq}, '{po_num}', {sup_id}, '{date_str}', '{exp_date}', '{rec_date}', '{po_status}', '{pay_status}', {total_po_amount}, 'Pengadaan rutin suku cadang operasional bengkel', {creator}, '{datetime_str}');"
        )

        for it in items_temp:
            po_items_sql_list.append(
                f"INSERT INTO purchase_order_items (po_id, part_id, quantity_ordered, quantity_received, unit_cost, subtotal) "
                f"VALUES ({it[0]}, {it[1]}, {it[2]}, {it[3]}, {it[4]}, {it[5]});"
            )

        po_id_seq += 1

    elif ev_type == "SERVICE_TRX":
        v_idx = ev_data
        trx_code = f"TRX-2022-{trx_id_seq:04d}"
        tech_id = random.choice([5, 6, 7, 8]) # technician agus, eko, dedi, rudi
        srv_template = random.choice(service_catalog)
        srv_name, part_code_list, labor_list = srv_template

        complaint, diag = random.choice(complaints)

        # Vehicle info
        v_data = vehicles_raw[v_idx - 1]
        odo_in = v_data[8] + random.randint(500, 3000)

        parts_total = 0
        used_parts = []
        for p_code in part_code_list:
            if p_code in parts_by_code:
                p_info = parts_by_code[p_code]
                p_id = p_info["id"]
                unit_sell = p_info["sell"]
                qty_used = random.randint(1, 2)
                subtotal = unit_sell * qty_used
                parts_total += subtotal

                used_parts.append((trx_id_seq, p_id, qty_used, unit_sell, subtotal))

                # Update stock tracker and write to stock ledger
                stock_before = part_stock_tracker[p_id]
                stock_after = max(0, stock_before - qty_used)
                part_stock_tracker[p_id] = stock_after

                ledger_sql_list.append(
                    f"INSERT INTO stock_ledger (transaction_time, part_id, transaction_type, reference_no, quantity_change, stock_before, stock_after, unit_cost, total_cost, notes, created_by) "
                    f"VALUES ('{datetime_str}', {p_id}, 'SERVICE_OUT', '{trx_code}', -{qty_used}, {stock_before}, {stock_after}, {p_info['cost']}, {p_info['cost'] * qty_used}, 'Pemakaian suku cadang PKB {trx_code}', {tech_id});"
                )

        labor_total = 0
        labors = []
        for lab in labor_list:
            l_name, l_hrs, l_rate = lab
            sub = int(l_hrs * l_rate)
            labor_total += sub
            labors.append((trx_id_seq, l_name, l_hrs, l_rate, sub))

        discount = 50000 if random.random() < 0.25 else 0
        sub_all = parts_total + labor_total - discount
        tax = int(sub_all * 0.11)
        grand = sub_all + tax

        pay_method = random.choice(["CASH", "TRANSFER", "DEBIT_CARD", "QRIS", "CREDIT_CARD"])
        pay_status = "PAID"
        trx_status = "COMPLETED"

        trx_sql_list.append(
            f"INSERT INTO service_transactions (id, transaction_code, transaction_date, vehicle_id, technician_id, service_type, odometer_in, customer_complaint, technician_diagnosis, parts_total, labor_total, discount, tax_amount, grand_total, status, payment_method, payment_status, created_by, created_at) "
            f"VALUES ({trx_id_seq}, '{trx_code}', '{datetime_str}', {v_idx}, {tech_id}, {escape_sql(srv_name)}, {odo_in}, {escape_sql(complaint)}, {escape_sql(diag)}, {parts_total}, {labor_total}, {discount}, {tax}, {grand}, '{trx_status}', '{pay_method}', '{pay_status}', {random.choice([3, 4])}, '{datetime_str}');"
        )

        for up in used_parts:
            trx_parts_sql_list.append(
                f"INSERT INTO service_parts_used (service_transaction_id, part_id, quantity, unit_price, subtotal) "
                f"VALUES ({up[0]}, {up[1]}, {up[2]}, {up[3]}, {up[4]});"
            )

        for lb in labors:
            trx_labor_sql_list.append(
                f"INSERT INTO service_labor_items (service_transaction_id, service_name, hours, rate, subtotal) "
                f"VALUES ({lb[0]}, {escape_sql(lb[1])}, {lb[2]}, {lb[3]}, {lb[4]});"
            )

        trx_id_seq += 1

    elif ev_type == "STOCK_ADJUSTMENT":
        adj_code = f"ADJ-2022-{adj_id_seq:04d}"
        # Pick a part to adjust
        p_code = random.choice(part_ids_list)
        p_info = parts_by_code[p_code]
        p_id = p_info["id"]

        adj_type = random.choice(["DECREASE", "DECREASE", "INCREASE"])
        qty = random.randint(1, 3)
        reason = random.choice(["DAMAGED_PART", "EXPIRED_FLUID", "PHYSICAL_COUNT_VARIANCE", "RETURN_FROM_CUSTOMER", "SCRAP_OBSOLETE"])
        notes = f"Audit stok bulanan: {reason.lower().replace('_', ' ')}"
        approver = random.choice([1, 2]) # Admin Bambang or Supervisor Hendra

        adj_sql_list.append(
            f"INSERT INTO stock_adjustments (adjustment_code, adjustment_date, part_id, adjustment_type, quantity, reason, notes, approved_by, created_at) "
            f"VALUES ('{adj_code}', '{date_str}', {p_id}, '{adj_type}', {qty}, '{reason}', {escape_sql(notes)}, {approver}, '{datetime_str}');"
        )

        stock_before = part_stock_tracker[p_id]
        if adj_type == "INCREASE":
            stock_after = stock_before + qty
            qty_change = qty
            ledger_type = "ADJUSTMENT_ADD"
        else:
            stock_after = max(0, stock_before - qty)
            qty_change = -qty
            ledger_type = "ADJUSTMENT_SUB"
        part_stock_tracker[p_id] = stock_after

        ledger_sql_list.append(
            f"INSERT INTO stock_ledger (transaction_time, part_id, transaction_type, reference_no, quantity_change, stock_before, stock_after, unit_cost, total_cost, notes, created_by) "
            f"VALUES ('{datetime_str}', {p_id}, '{ledger_type}', '{adj_code}', {qty_change}, {stock_before}, {stock_after}, {p_info['cost']}, {p_info['cost'] * qty}, {escape_sql(notes)}, {approver});"
        )

        adj_id_seq += 1

# Append all sections to SQL
out_sql.append("-- 7. PURCHASE ORDERS")
out_sql.extend(po_sql_list)
out_sql.append("")

out_sql.append("-- 8. PURCHASE ORDER ITEMS")
out_sql.extend(po_items_sql_list)
out_sql.append("")

out_sql.append("-- 9. SERVICE TRANSACTIONS")
out_sql.extend(trx_sql_list)
out_sql.append("")

out_sql.append("-- 10. SERVICE PARTS USED")
out_sql.extend(trx_parts_sql_list)
out_sql.append("")

out_sql.append("-- 11. SERVICE LABOR ITEMS")
out_sql.extend(trx_labor_sql_list)
out_sql.append("")

out_sql.append("-- 12. STOCK ADJUSTMENTS")
out_sql.extend(adj_sql_list)
out_sql.append("")

out_sql.append("-- 13. STOCK LEDGER AUDIT LOG")
out_sql.extend(ledger_sql_list)
out_sql.append("")

# Synchronize final parts.current_stock with tracked ending balance
out_sql.append("-- 14. SYNC PARTS ENDING CURRENT STOCK")
for code, info in parts_by_code.items():
    final_stock = max(0, part_stock_tracker[info["id"]])
    status = "OUT_OF_STOCK" if final_stock == 0 else ("LOW_STOCK" if final_stock <= info["min"] else "IN_STOCK")
    out_sql.append(f"UPDATE parts SET current_stock = {final_stock}, status = '{status}' WHERE id = {info['id']};")

with open("sql/seed_data_2022.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(out_sql))

print(f"Generated seed_data_2022.sql successfully!")
print(f"Total Purchase Orders: {po_id_seq - 1}")
print(f"Total Service Transactions: {trx_id_seq - 1}")
print(f"Total Stock Adjustments: {adj_id_seq - 1}")
print(f"Total Stock Ledger Entries: {len(ledger_sql_list)}")
