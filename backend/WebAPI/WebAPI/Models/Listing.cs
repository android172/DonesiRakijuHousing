using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebAPI.Models
{

    public class Listing
    {
        public uint Id { get; set; }
        public decimal Price { get; set; }
        public string Location { get; set; }
        public decimal Size { get; set; }
        public ResidenceType ResidenceType { get; set; }
        public StructureType StructureType { get; set; }
        public SaleType SaleType { get; set; }

    }
}