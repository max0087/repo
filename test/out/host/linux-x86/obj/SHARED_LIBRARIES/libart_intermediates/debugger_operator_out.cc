#include <iostream>

#include "debugger.h"

// This was automatically generated by art/tools/generate-operator-out.py --- do not edit!
namespace art {
std::ostream& operator<<(std::ostream& os, const Dbg::HpifWhen& rhs) {
  switch (rhs) {
    case Dbg::HPIF_WHEN_NEVER: os << "HPIF_WHEN_NEVER"; break;
    case Dbg::HPIF_WHEN_NOW: os << "HPIF_WHEN_NOW"; break;
    case Dbg::HPIF_WHEN_NEXT_GC: os << "HPIF_WHEN_NEXT_GC"; break;
    case Dbg::HPIF_WHEN_EVERY_GC: os << "HPIF_WHEN_EVERY_GC"; break;
    default: os << "Dbg::HpifWhen[" << static_cast<int>(rhs) << "]"; break;
  }
  return os;
}
}  // namespace art

// This was automatically generated by art/tools/generate-operator-out.py --- do not edit!
namespace art {
std::ostream& operator<<(std::ostream& os, const Dbg::HpsgWhen& rhs) {
  switch (rhs) {
    case Dbg::HPSG_WHEN_NEVER: os << "HPSG_WHEN_NEVER"; break;
    case Dbg::HPSG_WHEN_EVERY_GC: os << "HPSG_WHEN_EVERY_GC"; break;
    default: os << "Dbg::HpsgWhen[" << static_cast<int>(rhs) << "]"; break;
  }
  return os;
}
}  // namespace art

// This was automatically generated by art/tools/generate-operator-out.py --- do not edit!
namespace art {
std::ostream& operator<<(std::ostream& os, const Dbg::EventFlag& rhs) {
  switch (rhs) {
    case Dbg::kBreakpoint: os << "Breakpoint"; break;
    case Dbg::kSingleStep: os << "SingleStep"; break;
    case Dbg::kMethodEntry: os << "MethodEntry"; break;
    case Dbg::kMethodExit: os << "MethodExit"; break;
    default: os << "Dbg::EventFlag[" << static_cast<int>(rhs) << "]"; break;
  }
  return os;
}
}  // namespace art

// This was automatically generated by art/tools/generate-operator-out.py --- do not edit!
namespace art {
std::ostream& operator<<(std::ostream& os, const DeoptimizationRequest::Kind& rhs) {
  switch (rhs) {
    case DeoptimizationRequest::kNothing: os << "Nothing"; break;
    case DeoptimizationRequest::kRegisterForEvent: os << "RegisterForEvent"; break;
    case DeoptimizationRequest::kUnregisterForEvent: os << "UnregisterForEvent"; break;
    case DeoptimizationRequest::kFullDeoptimization: os << "FullDeoptimization"; break;
    case DeoptimizationRequest::kFullUndeoptimization: os << "FullUndeoptimization"; break;
    case DeoptimizationRequest::kSelectiveDeoptimization: os << "SelectiveDeoptimization"; break;
    case DeoptimizationRequest::kSelectiveUndeoptimization: os << "SelectiveUndeoptimization"; break;
    default: os << "DeoptimizationRequest::Kind[" << static_cast<int>(rhs) << "]"; break;
  }
  return os;
}
}  // namespace art

// This was automatically generated by art/tools/generate-operator-out.py --- do not edit!
namespace art {
std::ostream& operator<<(std::ostream& os, const Dbg::HpsgWhat& rhs) {
  switch (rhs) {
    case Dbg::HPSG_WHAT_MERGED_OBJECTS: os << "HPSG_WHAT_MERGED_OBJECTS"; break;
    case Dbg::HPSG_WHAT_DISTINCT_OBJECTS: os << "HPSG_WHAT_DISTINCT_OBJECTS"; break;
    default: os << "Dbg::HpsgWhat[" << static_cast<int>(rhs) << "]"; break;
  }
  return os;
}
}  // namespace art

