Feature: Style inheritance

  Scenario: a shape inherits fill from its group
    Given the document:
      """
      <svg xmlns="http://www.w3.org/2000/svg">
        <g fill="red"><rect width="10" height="10"/></g>
      </svg>
      """
    When it is rendered
    Then the node at "svg > g > rect" has fill "red"
