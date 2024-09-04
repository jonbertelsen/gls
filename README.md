# GLS 2

## Domain model

```plaintext
+----------------+      1   *      +----------------+
|    Package     |<----------------|    Shipment    |
+----------------+                 +----------------+
| ID             |                 | ID             |
| TrackingNumber |                 | ShipmentDate   |
+----------------+                 +----------------+
                                           | 1   | 1
                                           | *   | *
                            +----------------+ +----------------+
                            |   Location     | |   Location     |
                            +----------------+ +----------------+
                            | ID             | | ID             |
                            | Latitude       | | Latitude       |
                            | Longitude      | | Longitude      |
                            | Address        | | Address        |
                            +----------------+ +----------------+
```

```mermaid
classDiagram
  Package --o Shipment : "1 to *"
  Shipment --|> Location : "1 to 1 (Source)"
  Shipment --|> Location : "1 to 1 (Destination)"

  class Package {
    Long id
    String trackingNumber
  }

  class Shipment {
    Long id
    LocalDateTime shipmentDateTime
  }

  class Location {
    Long id
    Double latitude
    Double longitude
    String address
  }
```